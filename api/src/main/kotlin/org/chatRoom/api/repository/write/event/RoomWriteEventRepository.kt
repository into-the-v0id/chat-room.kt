package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.event.room.ChangeHandle
import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.repository.write.RoomWriteRepository
import javax.sql.DataSource

class RoomWriteEventRepository(
    dataSource: DataSource,
) : WriteEventRepository<RoomEvent>(dataSource, "room_events"), RoomWriteRepository {
    override fun serializeEvent(event: RoomEvent): JsonElement = when (event) {
        is CreateRoom -> Json.encodeToJsonElement(event)
        is ChangeHandle -> Json.encodeToJsonElement(event)
        is DeleteRoom -> Json.encodeToJsonElement(event)
    }

    override fun createAll(rooms: Collection<Room>) {
        val events = rooms.map { room -> room.events }.flatten()
        createAllEvents(events)
    }

    override fun updateAll(rooms: Collection<Room>) {
        val events = rooms.map { room -> room.events }.flatten()
        persistAllEvents(events)
    }

    override fun deleteAll(rooms: Collection<Room>) {
        val events = rooms.map { room -> DeleteRoom(modelId = room.modelId) }
        createAllEvents(events)
    }
}
