package org.chatRoom.api.repository.write.concurrent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.RoomWriteRepository

class RoomWriteConcurrentRepository(
    private val repositories: Collection<RoomWriteRepository>,
) : RoomWriteRepository {
    override suspend fun createAll(rooms: Collection<Room>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.createAll(rooms, transaction)
            }
        }
    }

    override suspend fun updateAll(rooms: Collection<Room>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.updateAll(rooms, transaction)
            }
        }
    }

    override suspend fun deleteAll(rooms: Collection<Room>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.deleteAll(rooms, transaction)
            }
        }
    }
}
