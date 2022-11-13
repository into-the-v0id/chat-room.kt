package org.chatRoom.api.repository.write.cascade

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.slf4j.LoggerFactory

class UserWriteCascadeRepository(
    private val repository: UserWriteRepository,
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
) : UserWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(UserWriteCascadeRepository::class.java)
    }

    override suspend fun createAll(users: Collection<User>, transaction: Transaction) = repository.createAll(users, transaction)

    override suspend fun updateAll(users: Collection<User>, transaction: Transaction) = repository.updateAll(users, transaction)

    override suspend fun deleteAll(users: Collection<User>, transaction: Transaction) {
        logger.info("Cascading deletion of all specified users to members")

        withContext(Dispatchers.Default) {
            launch {
                val members = memberReadRepository.getAll(userIds = users.map { user -> user.modelId })
                memberWriteRepository.deleteAll(members, transaction)
            }

            launch {
                repository.deleteAll(users, transaction)
            }
        }
    }
}
