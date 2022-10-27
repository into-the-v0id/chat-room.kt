package org.chatRoom.core.repository.write.chain

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteChainRepository(
    private val repositories: List<RoomWriteRepository>,
) : RoomWriteRepository {
    override fun create(room: Room) = repositories.forEach { repository -> repository.create(room) }

    override fun update(room: Room) = repositories.forEach { repository -> repository.update(room) }

    override fun delete(room: Room) = repositories.forEach { repository -> repository.delete(room) }
}
