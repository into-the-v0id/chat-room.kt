package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteChainRepository(
    private val repositories: Collection<UserWriteRepository>,
) : UserWriteRepository {
    override suspend fun createAll(users: Collection<User>, transaction: Transaction) = repositories.forEach { repository ->
        repository.createAll(users, transaction)
    }

    override suspend fun updateAll(users: Collection<User>, transaction: Transaction) = repositories.forEach { repository ->
        repository.updateAll(users, transaction)
    }

    override suspend fun deleteAll(users: Collection<User>, transaction: Transaction) = repositories.forEach { repository ->
        repository.deleteAll(users, transaction)
    }
}
