package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteGuardRepository(
    private val repository: RoomWriteRepository,
    private val roomReadRepository: RoomReadRepository,
) : RoomWriteRepository {
    override fun create(room: Room) {
        if (roomReadRepository.getById(room.modelId) != null) error("Unable to create room: Room already exists")
        if (roomReadRepository.getAll(handles = listOf(room.handle)).isNotEmpty()) error("Unable to create room: Handle already exists")

        repository.create(room)
    }

    override fun update(room: Room) {
        if (roomReadRepository.getById(room.modelId) == null) error("Unable to update room: Room not found")

        repository.update(room)
    }

    override fun delete(room: Room) {
        if (roomReadRepository.getById(room.modelId) == null) error("Unable to delete room: Room not found")

        repository.delete(room)
    }
}
