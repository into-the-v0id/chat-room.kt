package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.MessageWriteRepository
import javax.sql.DataSource

class MessageWriteEventRepository(
    private val dataSource: DataSource,
) : WriteEventRepository<MessageEvent>("message_events"), MessageWriteRepository {
    override fun serializeEvent(event: MessageEvent): JsonElement = when (event) {
        is CreateMessage -> Json.encodeToJsonElement(event)
        is ChangeContent -> Json.encodeToJsonElement(event)
        is DeleteMessage -> Json.encodeToJsonElement(event)
    }

    override suspend fun createAll(messages: Collection<Message>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = messages.map { message -> message.events }.flatten()
        createAllEvents(events, connection)
    }

    override suspend fun updateAll(messages: Collection<Message>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = messages.map { message -> message.events }.flatten()
        persistAllEvents(events, connection)
    }

    override suspend fun deleteAll(messages: Collection<Message>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = messages.map { message -> DeleteMessage(modelId = message.modelId) }
        createAllEvents(events, connection)
    }
}
