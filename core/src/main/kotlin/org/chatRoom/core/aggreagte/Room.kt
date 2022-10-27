package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.room.ChangeHandle
import org.chatRoom.core.event.room.CreateRoom
import org.chatRoom.core.event.room.DeleteRoom
import org.chatRoom.core.event.room.RoomEvent
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import java.time.Instant

class Room protected constructor(
    modelId: Id,
    handle: Handle,
    dateCreated: Instant = Instant.now(),
    dateUpdated: Instant = dateCreated,
) : Aggregate<RoomEvent>(modelId = modelId) {
    var handle: Handle = handle
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    var dateUpdated: Instant = dateUpdated
        protected set

    companion object {
        fun create(handle: Handle): Room {
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
                is ChangeHandle -> {
                    if (room == null) error("Expected room")
                    room.handle = event.handle
                    room.dateUpdated = event.dateIssued
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

    fun changeHandle(handle: Handle): Room {
        val event = ChangeHandle(
            modelId = this.modelId,
            handle = handle,
        )

        return applyEvent(this, event) ?: error("Expected room")
    }
}
