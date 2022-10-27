package org.chatRoom.core.repository.write.event

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
    override fun serializeEvent(event: RoomEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateRoom -> CreateRoom::class.java.name to Json.encodeToJsonElement(event)
            is ChangeHandle -> ChangeHandle::class.java.name to Json.encodeToJsonElement(event)
            is DeleteRoom -> DeleteRoom::class.java.name to Json.encodeToJsonElement(event)
        }
    }

    override fun create(room: Room) = createAllEvents(room.events)

    override fun update(room: Room) = persistAllEvents(room.events)

    override fun delete(room: Room) = createEvent(DeleteRoom(modelId = room.modelId))
}
