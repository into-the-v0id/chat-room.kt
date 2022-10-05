package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.valueObject.Id
import java.sql.Connection

class MemberRepository(
    connection: Connection,
    private val messageRepository: MessageRepository,
) : EventRepository<MemberEvent>(connection, "member_events") {
    override fun serializeEvent(event: MemberEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateMember -> CreateMember::class.java.name to Json.encodeToJsonElement(event)
            is DeleteMember -> DeleteMember::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): MemberEvent {
        return when (type) {
            CreateMember::class.java.name -> Json.decodeFromJsonElement<CreateMember>(data)
            DeleteMember::class.java.name -> Json.decodeFromJsonElement<DeleteMember>(data)
            else -> error("Unknown event type")
        }
    }

    fun create(member: Member) {
        if (getById(member.modelId) != null) error("Unable to create member: Member already exists")

        createAllEvents(member.events)
    }

    fun update(member: Member) {
        if (getById(member.modelId) == null) error("Unable to update member: Member not found")

        persistAllEvents(member.events)
    }

    fun delete(member: Member) {
        if (getById(member.modelId) == null) error("Unable to delete member: Member not found")

        val messages = messageRepository.getAll(memberIds = listOf(member.modelId))
        messages.forEach { message -> messageRepository.delete(message) }

        createEvent(DeleteMember(modelId = member.modelId))
    }

    fun getById(id: Id): Member? {
        val sql = """
            SELECT *
            FROM $tableName
            WHERE model_id = ?::uuid
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        statement.setString(1, id.toString())

        val resultSet = statement.executeQuery()
        val events = parseAllEvents(resultSet)
        if (events.isEmpty()) return null

        return Member.applyAllEvents(null, events)
    }

    fun getAll(userIds: List<Id>? = null, roomIds: List<Id>? = null): Collection<Member> {
        val conditions = mutableListOf("TRUE")
        if (roomIds != null) {
            conditions.add("""
                event_id IN (
                    SELECT event_id
                    FROM $tableName
                    WHERE (event_type = '${CreateMember::class.java.name}' AND event_data->>'roomId' = ANY(?))
                )
            """.trimIndent())
        }
        if (userIds != null) {
            conditions.add("""
                event_id IN (
                    SELECT event_id
                    FROM $tableName
                    WHERE (event_type = '${CreateMember::class.java.name}' AND event_data->>'userId' = ANY(?))
                )
            """.trimIndent())
        }

        val sql = """
            SELECT *
            FROM $tableName
            WHERE ${conditions.map { "($it)" }.joinToString(" AND ")}
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        var parameterCount = 0
        if (roomIds != null) {
            parameterCount += 1
            statement.setArray(
                parameterCount,
                connection.createArrayOf("text", roomIds.map { id -> id.toString() }.toTypedArray())
            )
        }
        if (userIds != null) {
            parameterCount += 1
            statement.setArray(
                parameterCount,
                connection.createArrayOf("text", userIds.map { id -> id.toString() }.toTypedArray())
            )
        }

        val resultSet = statement.executeQuery()
        val allEvents = parseAllEvents(resultSet)

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Member.applyAllEvents(null, events) }
            .filterNotNull()
            .filter { member ->
                if (roomIds != null && member.roomId !in roomIds) {
                    return@filter false
                }

                if (userIds != null && member.userId !in userIds) {
                    return@filter false
                }

                return@filter true
            }
    }
}
