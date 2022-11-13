package org.chatRoom.api.repository.write.chain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteChainRepository(
    private val repositories: Collection<UserWriteRepository>,
) : UserWriteRepository {
    override suspend fun createAll(users: Collection<User>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.createAll(users, transaction)
            }
        }
    }

    override suspend fun updateAll(users: Collection<User>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.updateAll(users, transaction)
            }
        }
    }

    override suspend fun deleteAll(users: Collection<User>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.deleteAll(users, transaction)
            }
        }
    }
}
