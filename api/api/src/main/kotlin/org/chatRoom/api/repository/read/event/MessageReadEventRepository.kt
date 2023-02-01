package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.repository.read.MessageQuery
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.valueObject.Id
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

    override fun getById(id: Id): Message? = getAll(MessageQuery(ids = listOf(id))).firstOrNull()

    override fun getAll(query: MessageQuery): Collection<Message> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (query.ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
            )

            require(query.memberIds == null) { "Unsupported filter" }
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
            .map { (_, events) -> Message.applyAllEvents(null, events) }
            .filterNotNull()
    }

    override fun count(query: MessageQuery): Int = getAll(query).size
}
