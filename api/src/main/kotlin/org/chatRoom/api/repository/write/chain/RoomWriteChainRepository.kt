package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteChainRepository(
    private val repositories: Collection<RoomWriteRepository>,
) : RoomWriteRepository {
    override fun createAll(rooms: Collection<Room>, transaction: Transaction) = repositories.forEach { repository ->
        repository.createAll(rooms, transaction)
    }

    override fun updateAll(rooms: Collection<Room>, transaction: Transaction) = repositories.forEach { repository ->
        repository.updateAll(rooms, transaction)
    }

    override fun deleteAll(rooms: Collection<Room>, transaction: Transaction) = repositories.forEach { repository ->
        repository.deleteAll(rooms, transaction)
    }
}
