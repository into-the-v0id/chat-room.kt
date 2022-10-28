package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteChainRepository(
    private val repositories: List<RoomWriteRepository>,
) : RoomWriteRepository {
    override fun createAll(rooms: List<Room>) = repositories.forEach { repository -> repository.createAll(rooms) }

    override fun updateAll(rooms: List<Room>) = repositories.forEach { repository -> repository.updateAll(rooms) }

    override fun deleteAll(rooms: List<Room>) = repositories.forEach { repository -> repository.deleteAll(rooms) }
}
