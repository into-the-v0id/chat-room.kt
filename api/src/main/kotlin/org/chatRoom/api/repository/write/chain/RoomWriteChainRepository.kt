package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteChainRepository(
    private val repositories: Collection<RoomWriteRepository>,
) : RoomWriteRepository {
    override suspend fun createAll(rooms: Collection<Room>, transaction: Transaction) = repositories.forEach { repository ->
        repository.createAll(rooms, transaction)
    }

    override suspend fun updateAll(rooms: Collection<Room>, transaction: Transaction) = repositories.forEach { repository ->
        repository.updateAll(rooms, transaction)
    }

    override suspend fun deleteAll(rooms: Collection<Room>, transaction: Transaction) = repositories.forEach { repository ->
        repository.deleteAll(rooms, transaction)
    }
}
