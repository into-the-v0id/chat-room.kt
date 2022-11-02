package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.repository.write.MessageWriteRepository
import javax.sql.DataSource

class MessageWriteEventRepository(
    dataSource: DataSource,
) : WriteEventRepository<MessageEvent>(dataSource, "message_events"), MessageWriteRepository {
    override fun serializeEvent(event: MessageEvent): JsonElement {
        return when (event) {
            is CreateMessage -> Json.encodeToJsonElement(event)
            is ChangeContent -> Json.encodeToJsonElement(event)
            is DeleteMessage -> Json.encodeToJsonElement(event)
        }
    }

    override fun createAll(messages: List<Message>) {
        val events = messages.map { message -> message.events }.flatten()
        createAllEvents(events)
    }

    override fun updateAll(messages: List<Message>) {
        val events = messages.map { message -> message.events }.flatten()
        persistAllEvents(events)
    }

    override fun deleteAll(messages: List<Message>) {
        val events = messages.map { message -> DeleteMessage(modelId = message.modelId) }
        createAllEvents(events)
    }
}
