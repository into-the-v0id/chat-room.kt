package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.valueObject.Id
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MessageRepository(dataSource: DataSource) : EventRepository<MessageEvent>(dataSource, "message_events") {
    override fun serializeEvent(event: MessageEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateMessage -> CreateMessage::class.java.name to Json.encodeToJsonElement(event)
            is ChangeContent -> ChangeContent::class.java.name to Json.encodeToJsonElement(event)
            is DeleteMessage -> DeleteMessage::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): MessageEvent {
        return when (type) {
            CreateMessage::class.java.name -> Json.decodeFromJsonElement<CreateMessage>(data)
            ChangeContent::class.java.name -> Json.decodeFromJsonElement<ChangeContent>(data)
            DeleteMessage::class.java.name -> Json.decodeFromJsonElement<DeleteMessage>(data)
            else -> error("Unknown event type")
        }
    }

    fun create(message: Message) {
        if (getById(message.modelId) != null) error("Unable to create message: Message already exists")

        createAllEvents(message.events)
    }

    fun update(message: Message) {
        if (getById(message.modelId) == null) error("Unable to update message: Message not found")

        persistAllEvents(message.events)
    }

    fun delete(message: Message) {
        if (getById(message.modelId) == null) error("Unable to delete message: Message not found")

        createEvent(DeleteMessage(modelId = message.modelId))
    }

    fun getById(id: Id): Message? {
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

    fun getAll(memberIds: List<Id>? = null): Collection<Message> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (memberIds != null) {
                val subquery = DSL.select(DSL.field("model_id"))
                    .from(DSL.table(tableName))
                    .where(
                        DSL.field("event_type").eq(CreateMessage::class.java.name),
                        DSL.condition(
                            "event_data->>'memberId' = ANY(?)",
                            memberIds.map { id -> id.toString() }.toTypedArray(),
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
            .map { (_, events) -> Message.applyAllEvents(null, events) }
            .filterNotNull()
            .filter { message ->
                if (memberIds != null && message.memberId !in memberIds) {
                    return@filter false
                }

                return@filter true
            }
    }
}
