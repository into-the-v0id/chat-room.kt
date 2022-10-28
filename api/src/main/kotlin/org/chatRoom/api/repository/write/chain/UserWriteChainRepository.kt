package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteChainRepository(
    private val repositories: List<UserWriteRepository>,
) : UserWriteRepository {
    override fun createAll(users: List<User>) = repositories.forEach { repository -> repository.createAll(users) }

    override fun updateAll(users: List<User>) = repositories.forEach { repository -> repository.updateAll(users) }

    override fun deleteAll(users: List<User>) = repositories.forEach { repository -> repository.deleteAll(users) }
}
