package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.valueObject.Id
import java.time.Instant

class Room protected constructor(
    modelId: Id,
    handle: String,
    dateCreated: Instant = Instant.now(),
) : Aggregate<RoomEvent>(modelId = modelId) {
    var handle: String = handle
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    companion object {
        fun create(handle: String): Room {
            val event = CreateRoom(
                modelId = Id(),
                handle = handle,
            )

            return applyEvent(null, event) ?: error("Expected room")
        }

        fun applyEvent(room: Room?, event: RoomEvent): Room? = applyEvent(room, event, Companion::applyEventInternal)

        fun applyAllEvents(room: Room?, events: List<RoomEvent>): Room? = applyAllEvents(room, events, Companion::applyEventInternal)

        private fun applyEventInternal(room: Room?, event: RoomEvent): Room? {
            var room = room

            when (event) {
                is CreateRoom -> {
                    if (room != null) error("Unexpected room")
                    room = Room(
                        modelId = event.modelId,
                        handle = event.handle,
                        dateCreated = event.dateIssued,
                    )
                }
                is DeleteRoom -> {
                    if (room == null) error("Expected room")
                    room = null
                }
                else -> error("Unknown event")
            }

            return room
        }
    }
}
