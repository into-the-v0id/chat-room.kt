package org.chatRoom.core.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.valueObject.Id
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MessageReadEventRepository(
    dataSource: DataSource
) : ReadEventRepository<MessageEvent>(dataSource, "message_events"), MessageReadRepository {
    override fun deserializeEvent(type: String, data: JsonElement): MessageEvent {
        return when (type) {
            CreateMessage::class.java.name -> Json.decodeFromJsonElement<CreateMessage>(data)
            ChangeContent::class.java.name -> Json.decodeFromJsonElement<ChangeContent>(data)
            DeleteMessage::class.java.name -> Json.decodeFromJsonElement<DeleteMessage>(data)
            else -> error("Unknown event type")
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

    override fun getAll(ids: List<Id>?, memberIds: List<Id>?): Collection<Message> {
        val allEvents = dataSource.connection.use { connection ->
            if (ids != null) error("Unsupported filter")
            if (memberIds != null) error("Unsupported filter")

            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Message.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
