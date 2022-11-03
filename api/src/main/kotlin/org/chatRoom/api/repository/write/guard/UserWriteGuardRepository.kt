package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteGuardRepository(
    private val repository: UserWriteRepository,
    private val userReadRepository: UserReadRepository,
) : UserWriteRepository {
    override fun createAll(users: Collection<User>) {
        val userIds = users.map { user -> user.modelId }
        if (userReadRepository.getAll(ids = userIds).isNotEmpty()) error("Unable to create all specified users: User already exists")

        val userHandles = users.map { user -> user.handle }
        if (userReadRepository.getAll(handles = userHandles).isNotEmpty()) error("Unable to create all specified users: Handle already exists")

        repository.createAll(users)
    }

    override fun updateAll(users: Collection<User>) {
        val userIds = users.map { user -> user.modelId }
        val allIdsExist = userReadRepository.getAll(ids = userIds)
            .map { user -> user.modelId }
            .containsAll(userIds)
        if (! allIdsExist) error("Unable to update all specified users: User not found")

        repository.updateAll(users)
    }

    override fun deleteAll(users: Collection<User>) {
        val userIds = users.map { user -> user.modelId }
        val allIdsExist = userReadRepository.getAll(ids = userIds)
            .map { user -> user.modelId }
            .containsAll(userIds)
        if (! allIdsExist) error("Unable to delete all specified users: User not found")

        repository.deleteAll(users)
    }
}
