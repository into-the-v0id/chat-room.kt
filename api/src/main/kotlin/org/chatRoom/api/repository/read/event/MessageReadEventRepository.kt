package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.message.MessageSortCriterion
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class MessageReadEventRepository(
    dataSource: DataSource
) : ReadEventRepository<MessageEvent>(dataSource, "message_events"), MessageReadRepository {
    override fun deserializeEvent(data: JsonElement): MessageEvent {
        return when (data.jsonObject["eventType"]?.jsonPrimitive?.content) {
            CreateMessage.eventType -> Json.decodeFromJsonElement<CreateMessage>(data)
            ChangeContent.eventType -> Json.decodeFromJsonElement<ChangeContent>(data)
            DeleteMessage.eventType -> Json.decodeFromJsonElement<DeleteMessage>(data)
            else -> throw IllegalArgumentException("Unknown event type")
        }
    }

    override fun getById(id: Id): Message? {
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

        return Message.applyAllEvents(null, events)
    }

    override fun getAll(
        ids: List<Id>?,
        memberIds: List<Id>?,
        offset: Offset?,
        limit: Limit?,
        sortCriteria: List<MessageSortCriterion>,
    ): Collection<Message> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) {
                conditions.add(
                    DSL.field("model_id")
                        .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            if (memberIds != null) error("Unsupported filter")
            if (offset != null) error("Unsupported filter")
            if (limit != null) error("Unsupported filter")
            if (sortCriteria.isNotEmpty()) error("Custom sort criteria not supported")

            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(conditions)
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Message.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
