package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.event.room.ChangeHandle
import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.repository.read.RoomQuery
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.valueObject.*
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class RoomReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<RoomEvent>(dataSource, "room_events"), RoomReadRepository {
    override fun deserializeEvent(data: JsonElement): RoomEvent {
        return when (data.jsonObject["eventType"]?.jsonPrimitive?.content) {
            CreateRoom.eventType -> Json.decodeFromJsonElement<CreateRoom>(data)
            ChangeHandle.eventType -> Json.decodeFromJsonElement<ChangeHandle>(data)
            DeleteRoom.eventType -> Json.decodeFromJsonElement<DeleteRoom>(data)
            else -> throw IllegalArgumentException("Unknown event type")
        }
    }

    override fun getById(id: Id): Room? {
        val events = dataSource.connection.use { connection ->
            val fetch = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(DSL.field("model_id").eq(id.toUuid()))
                .orderBy(DSL.field("date_issued").asc())

            val result = fetch.fetch()
            parseAllEvents(result)
        }

        if (events.isEmpty()) return null

        return Room.applyAllEvents(null, events)
    }

    override fun getAll(query: RoomQuery): Collection<Room> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (query.ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
            )

            require(query.handles == null) { "Unsupported filter" }
            require(query.offset == null) { "Unsupported filter" }
            require(query.limit == null) { "Unsupported filter" }
            require(query.sortCriteria.isEmpty()) { "Custom sort criteria not supported" }

            val fetch = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(conditions)
                .orderBy(DSL.field("date_issued").asc())

            val result = fetch.fetch()
            parseAllEvents(result)
        }

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Room.applyAllEvents(null, events) }
            .filterNotNull()
    }

    override fun count(query: RoomQuery): Int = getAll(query).size
}
