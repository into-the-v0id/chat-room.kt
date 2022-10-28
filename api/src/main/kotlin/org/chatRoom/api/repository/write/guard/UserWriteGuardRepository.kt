package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteGuardRepository(
    private val repository: UserWriteRepository,
    private val userReadRepository: UserReadRepository,
) : UserWriteRepository {
    override fun create(user: User) {
        if (userReadRepository.getById(user.modelId) != null) error("Unable to create user: User already exists")
        if (userReadRepository.getAll(handles = listOf(user.handle)).isNotEmpty()) error("Unable to create user: Handle already exists")

        repository.create(user)
    }

    override fun update(user: User) {
        if (userReadRepository.getById(user.modelId) == null) error("Unable to update user: User not found")

        repository.update(user)
    }

    override fun delete(user: User) {
        if (userReadRepository.getById(user.modelId) == null) error("Unable to delete user: User not found")

        repository.delete(user)
    }
}
