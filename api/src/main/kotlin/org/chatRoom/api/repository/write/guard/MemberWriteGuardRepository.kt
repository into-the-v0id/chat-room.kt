package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteGuardRepository(
    private val repository: MemberWriteRepository,
    private val memberReadRepository: MemberReadRepository,
) : MemberWriteRepository {
    override suspend fun createAll(members: Collection<Member>, transaction: Transaction) {
        val memberIds = members.map { member -> member.modelId }
        if (memberReadRepository.getAll(ids = memberIds).isNotEmpty()) error("Unable to create all specified members: Member already exists")

        repository.createAll(members, transaction)
    }

    override suspend fun updateAll(members: Collection<Member>, transaction: Transaction) {
        val memberIds = members.map { member -> member.modelId }
        val allIdsExist = memberReadRepository.getAll(ids = memberIds)
            .map { member -> member.modelId }
            .containsAll(memberIds)
        if (! allIdsExist) error("Unable to update all specified members: Member not found")

        repository.updateAll(members, transaction)
    }

    override suspend fun deleteAll(members: Collection<Member>, transaction: Transaction) {
        val memberIds = members.map { member -> member.modelId }
        val allIdsExist = memberReadRepository.getAll(ids = memberIds)
            .map { member -> member.modelId }
            .containsAll(memberIds)
        if (! allIdsExist) error("Unable to delete all specified members: Member not found")

        repository.deleteAll(members, transaction)
    }
}
