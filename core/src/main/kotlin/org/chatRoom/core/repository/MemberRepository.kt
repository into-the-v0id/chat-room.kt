package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.valueObject.Id
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MemberRepository(
    dataSource: DataSource,
    private val messageRepository: MessageRepository,
) : EventRepository<MemberEvent>(dataSource, "member_events") {
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
        val events = dataSource.connection.use { connection ->
            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(DSL.field("model_id").eq(id.toUuid()))
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        if (events.isEmpty()) return null

        return Member.applyAllEvents(null, events)
    }

    fun getAll(userIds: List<Id>? = null, roomIds: List<Id>? = null): Collection<Member> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (roomIds != null) {
                val subquery = DSL.select(DSL.field("model_id"))
                    .from(DSL.table(tableName))
                    .where(
                        DSL.field("event_type").eq(CreateMember::class.java.name),
                        DSL.condition(
                            "event_data->>'roomId' = ANY(?)",
                            roomIds.map { id -> id.toString() }.toTypedArray(),
                        )
                    )

                conditions.add(DSL.field("model_id").`in`(subquery))
            }

            if (userIds != null) {
                val subquery = DSL.select(DSL.field("model_id"))
                    .from(DSL.table(tableName))
                    .where(
                        DSL.field("event_type").eq(CreateMember::class.java.name),
                        DSL.condition(
                            "event_data->>'userId' = ANY(?)",
                            userIds.map { id -> id.toString() }.toTypedArray(),
                        )
                    )

                conditions.add(DSL.field("model_id").`in`(subquery))
            }

            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(conditions)
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

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
