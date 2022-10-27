package org.chatRoom.core.repository.write.chain

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteChainRepository(
    private val repositories: List<UserWriteRepository>,
) : UserWriteRepository {
    override fun create(user: User) = repositories.forEach { repository -> repository.create(user) }

    override fun update(user: User) = repositories.forEach { repository -> repository.update(user) }

    override fun delete(user: User) = repositories.forEach { repository -> repository.delete(user) }
}
