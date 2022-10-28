package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.slf4j.LoggerFactory

class UserWriteCascadeRepository(
    private val repository: UserWriteRepository,
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
) : UserWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(UserWriteCascadeRepository::class.java)
    }

    override fun create(user: User) = repository.create(user)

    override fun update(user: User) = repository.update(user)

    override fun delete(user: User) {
        logger.info("Cascading deletion of user to members")
        val members = memberReadRepository.getAll(userIds = listOf(user.modelId))
        members.forEach { member -> memberWriteRepository.delete(member) }

        repository.delete(user)
    }
}
