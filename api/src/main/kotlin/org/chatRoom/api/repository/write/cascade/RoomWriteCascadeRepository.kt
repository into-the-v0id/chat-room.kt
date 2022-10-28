package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.slf4j.LoggerFactory

class RoomWriteCascadeRepository(
    private val repository: RoomWriteRepository,
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
) : RoomWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(RoomWriteCascadeRepository::class.java)
    }

    override fun create(room: Room) = repository.create(room)

    override fun update(room: Room) = repository.update(room)

    override fun delete(room: Room) {
        logger.info("Cascading deletion of room to members")
        val members = memberReadRepository.getAll(roomIds = listOf(room.modelId))
        members.forEach { member -> memberWriteRepository.delete(member) }

        repository.delete(room)
    }
}
