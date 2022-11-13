package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteChainRepository(
    private val repositories: Collection<MessageWriteRepository>,
) : MessageWriteRepository {
    override suspend fun createAll(messages: Collection<Message>, transaction: Transaction) = repositories.forEach { repository ->
        repository.createAll(messages, transaction)
    }

    override suspend fun updateAll(messages: Collection<Message>, transaction: Transaction) = repositories.forEach { repository ->
        repository.updateAll(messages, transaction)
    }

    override suspend fun deleteAll(messages: Collection<Message>, transaction: Transaction) = repositories.forEach { repository ->
        repository.deleteAll(messages, transaction)
    }
}
