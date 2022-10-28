package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.chatRoom.core.repository.write.delete
import org.slf4j.LoggerFactory

class UserWriteCascadeRepository(
    private val repository: UserWriteRepository,
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
) : UserWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(UserWriteCascadeRepository::class.java)
    }

    override fun createAll(users: List<User>) = repository.createAll(users)

    override fun updateAll(users: List<User>) = repository.updateAll(users)

    override fun deleteAll(users: List<User>) {
        logger.info("Cascading deletion of all specified users to members")
        val members = memberReadRepository.getAll(userIds = users.map { user -> user.modelId })
        members.forEach { member -> memberWriteRepository.delete(member) }

        repository.deleteAll(users)
    }
}
