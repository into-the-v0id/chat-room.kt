package org.chatRoom.api.repository.write.cascade

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.MessageQuery
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.slf4j.LoggerFactory

class MemberWriteCascadeRepository(
    private val repository: MemberWriteRepository,
    private val messageReadRepository: MessageReadRepository,
    private val messageWriteRepository: MessageWriteRepository,
) : MemberWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(MemberWriteCascadeRepository::class.java)
    }

    override suspend fun createAll(members: Collection<Member>, transaction: Transaction) = repository.createAll(members, transaction)

    override suspend fun updateAll(members: Collection<Member>, transaction: Transaction) = repository.updateAll(members, transaction)

    override suspend fun deleteAll(members: Collection<Member>, transaction: Transaction) {
        withContext(Dispatchers.Default) {
            launch {
                logger.info("Cascading deletion of all specified members to messages")
                val messages = messageReadRepository.getAll(MessageQuery(memberIds = members.map { member -> member.modelId }))
                messageWriteRepository.deleteAll(messages, transaction)
            }

            launch {
                repository.deleteAll(members, transaction)
            }
        }
    }
}
