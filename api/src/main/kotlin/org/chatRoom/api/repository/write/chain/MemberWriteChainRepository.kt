package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteChainRepository(
    private val repositories: Collection<MemberWriteRepository>,
) : MemberWriteRepository {
    override fun createAll(members: Collection<Member>) = repositories.forEach { repository -> repository.createAll(members) }

    override fun updateAll(members: Collection<Member>) = repositories.forEach { repository -> repository.updateAll(members) }

    override fun deleteAll(members: Collection<Member>) = repositories.forEach { repository -> repository.deleteAll(members) }
}
