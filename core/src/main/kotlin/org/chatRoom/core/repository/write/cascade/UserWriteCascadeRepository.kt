package org.chatRoom.core.repository.write.cascade

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.UserWriteRepository

class UserWriteCascadeRepository(
    private val repository: UserWriteRepository,
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
) : UserWriteRepository {
    override fun create(user: User) = repository.create(user)

    override fun update(user: User) = repository.update(user)

    override fun delete(user: User) {
        val members = memberReadRepository.getAll(userIds = listOf(user.modelId))
        members.forEach { member -> memberWriteRepository.delete(member) }

        repository.delete(user)
    }
}
