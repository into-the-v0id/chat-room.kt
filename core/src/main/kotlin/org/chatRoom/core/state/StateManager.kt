package org.chatRoom.core.state

import org.chatRoom.core.repository.read.event.MemberReadEventRepository
import org.chatRoom.core.repository.read.event.MessageReadEventRepository
import org.chatRoom.core.repository.read.event.RoomReadEventRepository
import org.chatRoom.core.repository.read.event.UserReadEventRepository
import org.chatRoom.core.repository.read.state.MemberReadStateRepository
import org.chatRoom.core.repository.read.state.MessageReadStateRepository
import org.chatRoom.core.repository.read.state.RoomReadStateRepository
import org.chatRoom.core.repository.read.state.UserReadStateRepository
import org.chatRoom.core.repository.write.state.MemberWriteStateRepository
import org.chatRoom.core.repository.write.state.MessageWriteStateRepository
import org.chatRoom.core.repository.write.state.RoomWriteStateRepository
import org.chatRoom.core.repository.write.state.UserWriteStateRepository
import org.slf4j.LoggerFactory

class StateManager(
    private val userReadEventRepository: UserReadEventRepository,
    private val userReadStateRepository: UserReadStateRepository,
    private val userWriteStateRepository: UserWriteStateRepository,
    private val roomReadEventRepository: RoomReadEventRepository,
    private val roomReadStateRepository: RoomReadStateRepository,
    private val roomWriteStateRepository: RoomWriteStateRepository,
    private val memberReadEventRepository: MemberReadEventRepository,
    private val memberReadStateRepository: MemberReadStateRepository,
    private val memberWriteStateRepository: MemberWriteStateRepository,
    private val messageReadEventRepository: MessageReadEventRepository,
    private val messageReadStateRepository: MessageReadStateRepository,
    private val messageWriteStateRepository: MessageWriteStateRepository,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(StateManager::class.java)
    }

    fun replayAllEvents() {
        replayUserEvents()
        replayRoomEvents()
        replayMemberEvents()
        replayMessageEvents()
    }

    private fun replayUserEvents() {
        logger.info("Replaying user events ...")

        userReadStateRepository.getAll()
            .forEach { aggregate -> userWriteStateRepository.delete(aggregate) }
        userReadEventRepository.getAll()
            .forEach { aggregate -> userWriteStateRepository.create(aggregate) }

        logger.info("Successfully replayed user events")
    }

    private fun replayRoomEvents() {
        logger.info("Replaying room events ...")

        roomReadStateRepository.getAll()
            .forEach { aggregate -> roomWriteStateRepository.delete(aggregate) }
        roomReadEventRepository.getAll()
            .forEach { aggregate -> roomWriteStateRepository.create(aggregate) }

        logger.info("Successfully replayed room events")
    }

    private fun replayMemberEvents() {
        logger.info("Replaying member events ...")

        memberReadStateRepository.getAll()
            .forEach { aggregate -> memberWriteStateRepository.delete(aggregate) }
        memberReadEventRepository.getAll()
            .forEach { aggregate -> memberWriteStateRepository.create(aggregate) }

        logger.info("Successfully replayed member events")
    }

    private fun replayMessageEvents() {
        logger.info("Replaying message events ...")

        messageReadStateRepository.getAll()
            .forEach { aggregate -> messageWriteStateRepository.delete(aggregate) }
        messageReadEventRepository.getAll()
            .forEach { aggregate -> messageWriteStateRepository.create(aggregate) }

        logger.info("Successfully replayed message events")
    }
}
