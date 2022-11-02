package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.event.room.ChangeHandle
import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class RoomReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<RoomEvent>(dataSource, "room_events"), RoomReadRepository {
    override fun deserializeEvent(type: String, data: JsonElement): RoomEvent {
        return when (type) {
            CreateRoom::class.java.name -> Json.decodeFromJsonElement<CreateRoom>(data)
            ChangeHandle::class.java.name -> Json.decodeFromJsonElement<ChangeHandle>(data)
            DeleteRoom::class.java.name -> Json.decodeFromJsonElement<DeleteRoom>(data)
            else -> throw IllegalArgumentException("Unknown event type")
        }
    }

    override fun getById(id: Id): Room? {
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

        return Room.applyAllEvents(null, events)
    }

    override fun getAll(ids: List<Id>?, handles: List<Handle>?): List<Room> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) {
                conditions.add(
                    DSL.field("id")
                        .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            if (handles != null) error("Unsupported filter")

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
    }
}
