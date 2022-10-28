package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteChainRepository(
    private val repositories: List<MessageWriteRepository>,
) : MessageWriteRepository {
    override fun createAll(messages: List<Message>) = repositories.forEach { repository -> repository.createAll(messages) }

    override fun updateAll(messages: List<Message>) = repositories.forEach { repository -> repository.updateAll(messages) }

    override fun deleteAll(messages: List<Message>) = repositories.forEach { repository -> repository.deleteAll(messages) }
}
