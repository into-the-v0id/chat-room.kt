package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.valueObject.Id
import java.sql.Connection

class RoomRepository(connection: Connection) : EventRepository<RoomEvent>(connection, "room_events") {
    override fun serializeEvent(event: RoomEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateRoom -> CreateRoom::class.java.name to Json.encodeToJsonElement(event)
            is DeleteRoom -> DeleteRoom::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): RoomEvent {
        return when (type) {
            CreateRoom::class.java.name -> Json.decodeFromJsonElement<CreateRoom>(data)
            DeleteRoom::class.java.name -> Json.decodeFromJsonElement<DeleteRoom>(data)
            else -> error("Unknown event type")
        }
    }

    fun create(room: Room) {
        if (getById(room.modelId) != null) error("Unable to create room: Room already exists")
        // TODO: prevent duplicate handle

        persistAllEvents(room.events)
    }

    fun update(room: Room) {
        if (getById(room.modelId) == null) error("Unable to update room: Room not found")

        persistAllEvents(room.events)
    }

    fun delete(room: Room) {
        if (getById(room.modelId) == null) error("Unable to delete room: Room not found")

        insertEvent(DeleteRoom(modelId = room.modelId))
    }

    fun getById(id: Id): Room? {
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

        return Room.applyAllEvents(null, events)
    }

    fun getAll(): Collection<Room> {
        val sql = """
            SELECT *
            FROM $tableName
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)

        val resultSet = statement.executeQuery()
        val allEvents = parseAllEvents(resultSet)

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Room.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
