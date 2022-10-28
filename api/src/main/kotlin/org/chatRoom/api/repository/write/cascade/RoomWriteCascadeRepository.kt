package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.chatRoom.core.repository.write.delete
import org.slf4j.LoggerFactory

class RoomWriteCascadeRepository(
    private val repository: RoomWriteRepository,
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
) : RoomWriteRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(RoomWriteCascadeRepository::class.java)
    }

    override fun createAll(rooms: List<Room>) = repository.createAll(rooms)

    override fun updateAll(rooms: List<Room>) = repository.updateAll(rooms)

    override fun deleteAll(rooms: List<Room>) {
        logger.info("Cascading deletion of all specified rooms to members")
        val members = memberReadRepository.getAll(roomIds = rooms.map { room -> room.modelId })
        members.forEach { member -> memberWriteRepository.delete(member) }

        repository.deleteAll(rooms)
    }
}
