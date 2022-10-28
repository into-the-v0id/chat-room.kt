package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteGuardRepository(
    private val repository: MessageWriteRepository,
    private val messageReadRepository: MessageReadRepository,
) : MessageWriteRepository {
    override fun create(message: Message) {
        if (messageReadRepository.getById(message.modelId) != null) error("Unable to create message: Message already exists")

        repository.create(message)
    }

    override fun update(message: Message) {
        if (messageReadRepository.getById(message.modelId) == null) error("Unable to update message: Message not found")

        repository.update(message)
    }

    override fun delete(message: Message) {
        if (messageReadRepository.getById(message.modelId) == null) error("Unable to delete message: Message not found")

        repository.delete(message)
    }
}
