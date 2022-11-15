package org.chatRoom.api.repository.write.guard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteGuardRepository(
    private val repository: UserWriteRepository,
    private val userReadRepository: UserReadRepository,
) : UserWriteRepository {
    override suspend fun createAll(users: Collection<User>, transaction: Transaction) {
        withContext(Dispatchers.Default) {
            val areUserIdsAvailable = async {
                val userIds = users.map { user -> user.modelId }
                userReadRepository.getAll(ids = userIds).isEmpty()
            }

            val areUserHandlesAvailable = async {
                val userHandles = users.map { user -> user.handle }
                userReadRepository.getAll(handles = userHandles).isEmpty()
            }

            if (! areUserIdsAvailable.await()) error("Unable to create all specified users: User already exists")
            if (! areUserHandlesAvailable.await()) error("Unable to create all specified users: Handle already exists")
        }

        repository.createAll(users, transaction)
    }

    override suspend fun updateAll(users: Collection<User>, transaction: Transaction) {
        val userIds = users.map { user -> user.modelId }
        val allIdsExist = userReadRepository.getAll(ids = userIds)
            .map { user -> user.modelId }
            .containsAll(userIds)
        if (! allIdsExist) error("Unable to update all specified users: User not found")

        repository.updateAll(users, transaction)
    }

    override suspend fun deleteAll(users: Collection<User>, transaction: Transaction) {
        val userIds = users.map { user -> user.modelId }
        val allIdsExist = userReadRepository.getAll(ids = userIds)
            .map { user -> user.modelId }
            .containsAll(userIds)
        if (! allIdsExist) error("Unable to delete all specified users: User not found")

        repository.deleteAll(users, transaction)
    }
}
