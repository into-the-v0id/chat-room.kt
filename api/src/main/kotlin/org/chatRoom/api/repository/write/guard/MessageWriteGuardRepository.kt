package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteGuardRepository(
    private val repository: MessageWriteRepository,
    private val messageReadRepository: MessageReadRepository,
) : MessageWriteRepository {
    override fun createAll(messages: Collection<Message>, transaction: Transaction) {
        val messageIds = messages.map { message -> message.modelId }
        if (messageReadRepository.getAll(ids = messageIds).isNotEmpty()) error("Unable to create all specified messages: Message already exists")

        repository.createAll(messages, transaction)
    }

    override fun updateAll(messages: Collection<Message>, transaction: Transaction) {
        val messageIds = messages.map { message -> message.modelId }
        val allIdsExist = messageReadRepository.getAll(ids = messageIds)
            .map { message -> message.modelId }
            .containsAll(messageIds)
        if (! allIdsExist) error("Unable to update all specified messages: Message not found")

        repository.updateAll(messages, transaction)
    }

    override fun deleteAll(messages: Collection<Message>, transaction: Transaction) {
        val messageIds = messages.map { message -> message.modelId }
        val allIdsExist = messageReadRepository.getAll(ids = messageIds)
            .map { message -> message.modelId }
            .containsAll(messageIds)
        if (! allIdsExist) error("Unable to delete all specified messages: Message not found")

        repository.deleteAll(messages, transaction)
    }
}
