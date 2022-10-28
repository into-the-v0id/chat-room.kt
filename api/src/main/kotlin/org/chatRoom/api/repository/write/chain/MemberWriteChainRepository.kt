package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteChainRepository(
    private val repositories: List<MemberWriteRepository>,
) : MemberWriteRepository {
    override fun createAll(members: List<Member>) = repositories.forEach { repository -> repository.createAll(members) }

    override fun updateAll(members: List<Member>) = repositories.forEach { repository -> repository.updateAll(members) }

    override fun deleteAll(members: List<Member>) = repositories.forEach { repository -> repository.deleteAll(members) }
}
