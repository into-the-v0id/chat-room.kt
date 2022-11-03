package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteChainRepository(
    private val repositories: Collection<RoomWriteRepository>,
) : RoomWriteRepository {
    override fun createAll(rooms: Collection<Room>) = repositories.forEach { repository -> repository.createAll(rooms) }

    override fun updateAll(rooms: Collection<Room>) = repositories.forEach { repository -> repository.updateAll(rooms) }

    override fun deleteAll(rooms: Collection<Room>) = repositories.forEach { repository -> repository.deleteAll(rooms) }
}
