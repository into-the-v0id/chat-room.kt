package org.chatRoom.core.repository.write.cascade

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.MessageWriteRepository

class MemberWriteCascadeRepository(
    private val repository: MemberWriteRepository,
    private val messageReadRepository: MessageReadRepository,
    private val messageWriteRepository: MessageWriteRepository,
) : MemberWriteRepository {
    override fun create(member: Member) = repository.create(member)

    override fun update(member: Member) = repository.update(member)

    override fun delete(member: Member) {
        val messages = messageReadRepository.getAll(memberIds = listOf(member.modelId))
        messages.forEach { message -> messageWriteRepository.delete(message) }

        repository.delete(member)
    }
}
