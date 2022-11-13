package org.chatRoom.api.state

import org.chatRoom.api.repository.read.event.MemberReadEventRepository
import org.chatRoom.api.repository.read.event.MessageReadEventRepository
import org.chatRoom.api.repository.read.event.RoomReadEventRepository
import org.chatRoom.api.repository.read.event.UserReadEventRepository
import org.chatRoom.api.repository.read.state.MemberReadStateRepository
import org.chatRoom.api.repository.read.state.MessageReadStateRepository
import org.chatRoom.api.repository.read.state.RoomReadStateRepository
import org.chatRoom.api.repository.read.state.UserReadStateRepository
import org.chatRoom.api.repository.write.state.MemberWriteStateRepository
import org.chatRoom.api.repository.write.state.MessageWriteStateRepository
import org.chatRoom.api.repository.write.state.RoomWriteStateRepository
import org.chatRoom.api.repository.write.state.UserWriteStateRepository
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id
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

        Transaction(id = Id()).execute { transaction ->
            userWriteStateRepository.deleteAll(userReadStateRepository.getAll(), transaction)
            userWriteStateRepository.createAll(userReadEventRepository.getAll(), transaction)
        }

        logger.info("Successfully replayed user events")
    }

    private fun replayRoomEvents() {
        logger.info("Replaying room events ...")

        Transaction(id = Id()).execute { transaction ->
            roomWriteStateRepository.deleteAll(roomReadStateRepository.getAll(), transaction)
            roomWriteStateRepository.createAll(roomReadEventRepository.getAll(), transaction)
        }

        logger.info("Successfully replayed room events")
    }

    private fun replayMemberEvents() {
        logger.info("Replaying member events ...")

        Transaction(id = Id()).execute { transaction ->
            memberWriteStateRepository.deleteAll(memberReadStateRepository.getAll(), transaction)
            memberWriteStateRepository.createAll(memberReadEventRepository.getAll(), transaction)
        }

        logger.info("Successfully replayed member events")
    }

    private fun replayMessageEvents() {
        logger.info("Replaying message events ...")

        Transaction(id = Id()).execute { transaction ->
            messageWriteStateRepository.deleteAll(messageReadStateRepository.getAll(), transaction)
            messageWriteStateRepository.createAll(messageReadEventRepository.getAll(), transaction)
        }

        logger.info("Successfully replayed message events")
    }
}
