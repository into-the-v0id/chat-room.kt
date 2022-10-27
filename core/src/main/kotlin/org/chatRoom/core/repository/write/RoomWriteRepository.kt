package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Room

interface RoomWriteRepository {
    fun create(room: Room)
    fun update(room: Room)
    fun delete(room: Room)
}
