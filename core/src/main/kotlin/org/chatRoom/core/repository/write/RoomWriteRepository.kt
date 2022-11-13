package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface RoomWriteRepository {
    fun createAll(rooms: Collection<Room>, transaction: Transaction)
    fun updateAll(rooms: Collection<Room>, transaction: Transaction)
    fun deleteAll(rooms: Collection<Room>, transaction: Transaction)
}

fun RoomWriteRepository.createAll(rooms: Collection<Room>) = Transaction(id = Id()).execute { transaction ->
    createAll(rooms, transaction)
}
fun RoomWriteRepository.updateAll(rooms: Collection<Room>) = Transaction(id = Id()).execute { transaction ->
    updateAll(rooms, transaction)
}
fun RoomWriteRepository.deleteAll(rooms: Collection<Room>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(rooms, transaction)
}
fun RoomWriteRepository.create(room: Room, transaction: Transaction) = createAll(listOf(room), transaction)
fun RoomWriteRepository.update(room: Room, transaction: Transaction) = updateAll(listOf(room), transaction)
fun RoomWriteRepository.delete(room: Room, transaction: Transaction) = deleteAll(listOf(room), transaction)
fun RoomWriteRepository.create(room: Room) = createAll(listOf(room))
fun RoomWriteRepository.update(room: Room) = updateAll(listOf(room))
fun RoomWriteRepository.delete(room: Room) = deleteAll(listOf(room))
