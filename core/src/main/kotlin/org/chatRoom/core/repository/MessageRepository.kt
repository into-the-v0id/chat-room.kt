package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.valueObject.Id
import java.sql.Connection

class MessageRepository(connection: Connection) : EventRepository<MessageEvent>(connection, "message_events") {
    override fun serializeEvent(event: MessageEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateMessage -> CreateMessage::class.java.name to Json.encodeToJsonElement(event)
            is DeleteMessage -> DeleteMessage::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): MessageEvent {
        return when (type) {
            CreateMessage::class.java.name -> Json.decodeFromJsonElement<CreateMessage>(data)
            DeleteMessage::class.java.name -> Json.decodeFromJsonElement<DeleteMessage>(data)
            else -> error("Unknown event type")
        }
    }

    fun create(message: Message) {
        if (getById(message.modelId) != null) error("Unable to create message: Message already exists")
        // TODO: prevent duplicate handles

        persistAllEvents(message.events)
    }

    fun update(message: Message) {
        if (getById(message.modelId) == null) error("Unable to update message: Message not found")

        persistAllEvents(message.events)
    }

    fun delete(message: Message) {
        if (getById(message.modelId) == null) error("Unable to delete message: Message not found")

        insertEvent(DeleteMessage(modelId = message.modelId))
    }

    fun getById(id: Id): Message? {
        val sql = """
            SELECT *
            FROM $tableName
            WHERE model_id = ?::uuid
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        statement.setString(1, id.toString())

        val resultSet = statement.executeQuery()
        val events = parseAllEvents(resultSet)
        if (events.isEmpty()) return null

        return Message.applyAllEvents(null, events)
    }

    fun getAll(): Collection<Message> {
        val sql = """
            SELECT *
            FROM $tableName
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)

        val resultSet = statement.executeQuery()
        val allEvents = parseAllEvents(resultSet)

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Message.applyAllEvents(null, events) }
            .filterNotNull()
    }
}