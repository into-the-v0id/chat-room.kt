package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteChainRepository(
    private val repositories: Collection<MemberWriteRepository>,
) : MemberWriteRepository {
    override fun createAll(members: Collection<Member>, transaction: Transaction) = repositories.forEach { repository ->
        repository.createAll(members, transaction)
    }

    override fun updateAll(members: Collection<Member>, transaction: Transaction) = repositories.forEach { repository ->
        repository.updateAll(members, transaction)
    }

    override fun deleteAll(members: Collection<Member>, transaction: Transaction) = repositories.forEach { repository ->
        repository.deleteAll(members, transaction)
    }
}
