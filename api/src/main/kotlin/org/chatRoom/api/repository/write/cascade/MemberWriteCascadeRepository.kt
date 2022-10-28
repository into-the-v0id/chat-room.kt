package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.Member
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

    override fun create(member: Member) = repository.create(member)

    override fun update(member: Member) = repository.update(member)

    override fun delete(member: Member) {
        logger.info("Cascading deletion of member to messages")
        val messages = messageReadRepository.getAll(memberIds = listOf(member.modelId))
        messages.forEach { message -> messageWriteRepository.delete(message) }

        repository.delete(member)
    }
}
