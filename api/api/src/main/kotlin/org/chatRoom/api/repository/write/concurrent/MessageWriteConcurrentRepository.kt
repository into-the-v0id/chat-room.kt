package org.chatRoom.api.repository.write.concurrent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteConcurrentRepository(
    private val repositories: Collection<MessageWriteRepository>,
) : MessageWriteRepository {
    override suspend fun createAll(messages: Collection<Message>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.createAll(messages, transaction)
            }
        }
    }

    override suspend fun updateAll(messages: Collection<Message>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.updateAll(messages, transaction)
            }
        }
    }

    override suspend fun deleteAll(messages: Collection<Message>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.deleteAll(messages, transaction)
            }
        }
    }
}
