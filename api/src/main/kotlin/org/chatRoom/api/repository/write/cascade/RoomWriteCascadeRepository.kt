package org.chatRoom.api.repository.write.cascade

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
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

    override suspend fun createAll(rooms: Collection<Room>, transaction: Transaction) = repository.createAll(rooms, transaction)

    override suspend fun updateAll(rooms: Collection<Room>, transaction: Transaction) = repository.updateAll(rooms, transaction)

    override suspend fun deleteAll(rooms: Collection<Room>, transaction: Transaction) {
        logger.info("Cascading deletion of all specified rooms to members")
        val members = memberReadRepository.getAll(roomIds = rooms.map { room -> room.modelId })
        memberWriteRepository.deleteAll(members, transaction)

        repository.deleteAll(rooms, transaction)
    }
}
