package org.chatRoom.core.repository.write.guard

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository

class MemberWriteGuardRepository(
    private val repository: MemberWriteRepository,
    private val memberReadRepository: MemberReadRepository,
) : MemberWriteRepository {
    override fun create(member: Member) {
        if (memberReadRepository.getById(member.modelId) != null) error("Unable to create member: Member already exists")

        repository.create(member)
    }

    override fun update(member: Member) {
        if (memberReadRepository.getById(member.modelId) == null) error("Unable to update member: Member not found")

        repository.update(member)
    }

    override fun delete(member: Member) {
        if (memberReadRepository.getById(member.modelId) == null) error("Unable to delete member: Member not found")

        repository.delete(member)
    }
}
