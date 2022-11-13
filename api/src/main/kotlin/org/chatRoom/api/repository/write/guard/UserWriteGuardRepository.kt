package org.chatRoom.api.repository.write.guard

import kotlinx.coroutines.Dispatchers
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
        var isUserIdAvailable: Boolean? = null
        var isUserHandleAvailable: Boolean? = null

        withContext(Dispatchers.Default) {
            launch {
                val userIds = users.map { user -> user.modelId }
                isUserIdAvailable = userReadRepository.getAll(ids = userIds).isEmpty()
            }

            launch {
                val userHandles = users.map { user -> user.handle }
                isUserHandleAvailable = userReadRepository.getAll(handles = userHandles).isEmpty()
            }
        }

        if (! isUserIdAvailable!!) error("Unable to create all specified users: User already exists")
        if (! isUserHandleAvailable!!) error("Unable to create all specified users: Handle already exists")

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
