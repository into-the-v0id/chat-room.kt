package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Room

interface RoomWriteRepository {
    fun createAll(rooms: Collection<Room>)
    fun updateAll(rooms: Collection<Room>)
    fun deleteAll(rooms: Collection<Room>)
}

fun RoomWriteRepository.create(room: Room) = createAll(listOf(room))
fun RoomWriteRepository.update(room: Room) = updateAll(listOf(room))
fun RoomWriteRepository.delete(room: Room) = deleteAll(listOf(room))
