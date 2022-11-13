package org.chatRoom.api.repository.write.concurrent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteConcurrentRepository(
    private val repositories: Collection<MemberWriteRepository>,
) : MemberWriteRepository {
    override suspend fun createAll(members: Collection<Member>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.createAll(members, transaction)
            }
        }
    }

    override suspend fun updateAll(members: Collection<Member>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.updateAll(members, transaction)
            }
        }
    }

    override suspend fun deleteAll(members: Collection<Member>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.deleteAll(members, transaction)
            }
        }
    }
}
