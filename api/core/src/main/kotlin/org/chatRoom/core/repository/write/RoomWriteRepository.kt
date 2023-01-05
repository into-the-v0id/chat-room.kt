package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface RoomWriteRepository {
    suspend fun createAll(rooms: Collection<Room>, transaction: Transaction)
    suspend fun updateAll(rooms: Collection<Room>, transaction: Transaction)
    suspend fun deleteAll(rooms: Collection<Room>, transaction: Transaction)
}

suspend fun RoomWriteRepository.createAll(rooms: Collection<Room>) = Transaction(id = Id()).execute { transaction ->
    createAll(rooms, transaction)
}
suspend fun RoomWriteRepository.updateAll(rooms: Collection<Room>) = Transaction(id = Id()).execute { transaction ->
    updateAll(rooms, transaction)
}
suspend fun RoomWriteRepository.deleteAll(rooms: Collection<Room>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(rooms, transaction)
}
suspend fun RoomWriteRepository.create(room: Room, transaction: Transaction) = createAll(listOf(room), transaction)
suspend fun RoomWriteRepository.update(room: Room, transaction: Transaction) = updateAll(listOf(room), transaction)
suspend fun RoomWriteRepository.delete(room: Room, transaction: Transaction) = deleteAll(listOf(room), transaction)
suspend fun RoomWriteRepository.create(room: Room) = createAll(listOf(room))
suspend fun RoomWriteRepository.update(room: Room) = updateAll(listOf(room))
suspend fun RoomWriteRepository.delete(room: Room) = deleteAll(listOf(room))
