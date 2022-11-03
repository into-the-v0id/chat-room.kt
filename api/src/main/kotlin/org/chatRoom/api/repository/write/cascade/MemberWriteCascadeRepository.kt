package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.chatRoom.core.repository.write.delete
import org.slf4j.LoggerFactory

class MemberWriteCascadeRepository(
    private val repository: MemberWriteRepository,
    private val messageReadRepository: MessageReadRepository,
    private val messageWriteRepository: MessageWriteRepository,
) : MemberWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(MemberWriteCascadeRepository::class.java)
    }

    override fun createAll(members: Collection<Member>) = repository.createAll(members)

    override fun updateAll(members: Collection<Member>) = repository.updateAll(members)

    override fun deleteAll(members: Collection<Member>) {
        logger.info("Cascading deletion of all specified members to messages")
        val messages = messageReadRepository.getAll(memberIds = members.map { member -> member.modelId })
        messages.forEach { message -> messageWriteRepository.delete(message) }

        repository.deleteAll(members)
    }
}
