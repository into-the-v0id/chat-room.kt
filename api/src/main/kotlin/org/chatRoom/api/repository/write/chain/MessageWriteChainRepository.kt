package org.chatRoom.api.repository.write.chain

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.write.MessageWriteRepository

class MessageWriteChainRepository(
    private val repositories: Collection<MessageWriteRepository>,
) : MessageWriteRepository {
    override fun createAll(messages: Collection<Message>) = repositories.forEach { repository -> repository.createAll(messages) }

    override fun updateAll(messages: Collection<Message>) = repositories.forEach { repository -> repository.updateAll(messages) }

    override fun deleteAll(messages: Collection<Message>) = repositories.forEach { repository -> repository.deleteAll(messages) }
}
