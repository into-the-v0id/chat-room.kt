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
    fun replayAllEvents() {
        replayUserEvents()
        replayRoomEvents()
        replayMemberEvents()
        replayMessageEvents()
    }

    private fun replayUserEvents() {
        userReadStateRepository.getAll()
            .forEach { aggregate -> userWriteStateRepository.delete(aggregate) }
        userReadEventRepository.getAll()
            .forEach { aggregate -> userWriteStateRepository.create(aggregate) }
    }

    private fun replayRoomEvents() {
        roomReadStateRepository.getAll()
            .forEach { aggregate -> roomWriteStateRepository.delete(aggregate) }
        roomReadEventRepository.getAll()
            .forEach { aggregate -> roomWriteStateRepository.create(aggregate) }
    }

    private fun replayMemberEvents() {
        memberReadStateRepository.getAll()
            .forEach { aggregate -> memberWriteStateRepository.delete(aggregate) }
        memberReadEventRepository.getAll()
            .forEach { aggregate -> memberWriteStateRepository.create(aggregate) }
    }

    private fun replayMessageEvents() {
        messageReadStateRepository.getAll()
            .forEach { aggregate -> messageWriteStateRepository.delete(aggregate) }
        messageReadEventRepository.getAll()
            .forEach { aggregate -> messageWriteStateRepository.create(aggregate) }
    }
}
