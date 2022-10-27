package org.chatRoom.core.repository.write.chain

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteChainRepository(
    private val repositories: List<MemberWriteRepository>,
) : MemberWriteRepository {
    override fun create(member: Member) = repositories.forEach { repository -> repository.create(member) }

    override fun update(member: Member) = repositories.forEach { repository -> repository.update(member) }

    override fun delete(member: Member) = repositories.forEach { repository -> repository.delete(member) }
}
