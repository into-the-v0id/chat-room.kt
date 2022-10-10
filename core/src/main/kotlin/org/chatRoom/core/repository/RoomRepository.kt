package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.event.room.ChangeHandle
import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class RoomRepository(
    dataSource: DataSource,
    private val memberRepository: MemberRepository,
) : EventRepository<RoomEvent>(dataSource, "room_events") {
    override fun serializeEvent(event: RoomEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateRoom -> CreateRoom::class.java.name to Json.encodeToJsonElement(event)
            is ChangeHandle -> ChangeHandle::class.java.name to Json.encodeToJsonElement(event)
            is DeleteRoom -> DeleteRoom::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): RoomEvent {
        return when (type) {
            CreateRoom::class.java.name -> Json.decodeFromJsonElement<CreateRoom>(data)
            ChangeHandle::class.java.name -> Json.decodeFromJsonElement<ChangeHandle>(data)
            DeleteRoom::class.java.name -> Json.decodeFromJsonElement<DeleteRoom>(data)
            else -> error("Unknown event type")
        }
    }

    fun create(room: Room) {
        if (getById(room.modelId) != null) error("Unable to create room: Room already exists")
        if (getAll(handles = listOf(room.handle)).isNotEmpty()) error("Unable to create room: Handle already exists")

        createAllEvents(room.events)
    }

    fun update(room: Room) {
        if (getById(room.modelId) == null) error("Unable to update room: Room not found")

        persistAllEvents(room.events)
    }

    fun delete(room: Room) {
        if (getById(room.modelId) == null) error("Unable to delete room: Room not found")

        val members = memberRepository.getAll(roomIds = listOf(room.modelId))
        members.forEach { member -> memberRepository.delete(member) }

        createEvent(DeleteRoom(modelId = room.modelId))
    }

    fun getById(id: Id): Room? {
        val events = dataSource.connection.use { connection ->
            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where("model_id = ?::uuid", DSL.value(id.toString()))
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        if (events.isEmpty()) return null

        return Room.applyAllEvents(null, events)
    }

    fun getAll(handles: List<Handle>? = null): Collection<Room> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (handles != null) {
                val subquery = DSL.using(SQLDialect.POSTGRES)
                    .select(DSL.field("event_id"))
                    .from(DSL.table(tableName))
                    .where(
                        "(event_type = ? AND event_data->>'handle' = ANY(?))",
                        DSL.value(CreateRoom::class.java.name),
                        DSL.value(handles.map { handle -> handle.toString() }.toTypedArray()),
                    )

                conditions.add(DSL.condition("event_id IN (?)", subquery))
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
            .map { (_, events) -> Room.applyAllEvents(null, events) }
            .filterNotNull()
            .filter { member ->
                if (handles != null && member.handle !in handles) {
                    return@filter false
                }

                return@filter true
            }
    }
}
