package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteGuardRepository(
    private val repository: RoomWriteRepository,
    private val roomReadRepository: RoomReadRepository,
) : RoomWriteRepository {
    override suspend fun createAll(rooms: Collection<Room>, transaction: Transaction) {
        val roomIds = rooms.map { room -> room.modelId }
        if (roomReadRepository.getAll(ids = roomIds).isNotEmpty()) error("Unable to create all specified rooms: Room already exists")

        val roomHandles = rooms.map { room -> room.handle }
        if (roomReadRepository.getAll(handles = roomHandles).isNotEmpty()) error("Unable to create all specified rooms: Handle already exists")

        repository.createAll(rooms, transaction)
    }

    override suspend fun updateAll(rooms: Collection<Room>, transaction: Transaction) {
        val roomIds = rooms.map { room -> room.modelId }
        val allIdsExist = roomReadRepository.getAll(ids = roomIds)
            .map { room -> room.modelId }
            .containsAll(roomIds)
        if (! allIdsExist) error("Unable to update all specified rooms: Room not found")

        repository.updateAll(rooms, transaction)
    }

    override suspend fun deleteAll(rooms: Collection<Room>, transaction: Transaction) {
        val roomIds = rooms.map { room -> room.modelId }
        val allIdsExist = roomReadRepository.getAll(ids = roomIds)
            .map { room -> room.modelId }
            .containsAll(roomIds)
        if (! allIdsExist) error("Unable to delete all specified rooms: Room not found")

        repository.deleteAll(rooms, transaction)
    }
}
