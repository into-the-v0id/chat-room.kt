package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteChainRepository(
    private val repositories: List<MessageWriteRepository>,
) : MessageWriteRepository {
    override fun create(message: Message) = repositories.forEach { repository -> repository.create(message) }

    override fun update(message: Message) = repositories.forEach { repository -> repository.update(message) }

    override fun delete(message: Message) = repositories.forEach { repository -> repository.delete(message) }
}
