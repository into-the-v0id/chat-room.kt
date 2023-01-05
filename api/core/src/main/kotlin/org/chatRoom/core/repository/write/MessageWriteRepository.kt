package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface MessageWriteRepository {
    suspend fun createAll(messages: Collection<Message>, transaction: Transaction)
    suspend fun updateAll(messages: Collection<Message>, transaction: Transaction)
    suspend fun deleteAll(messages: Collection<Message>, transaction: Transaction)
}

suspend fun MessageWriteRepository.createAll(messages: Collection<Message>) = Transaction(id = Id()).execute { transaction ->
    createAll(messages, transaction)
}
suspend fun MessageWriteRepository.updateAll(messages: Collection<Message>) = Transaction(id = Id()).execute { transaction ->
    updateAll(messages, transaction)
}
suspend fun MessageWriteRepository.deleteAll(messages: Collection<Message>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(messages, transaction)
}
suspend fun MessageWriteRepository.create(message: Message, transaction: Transaction) = createAll(listOf(message), transaction)
suspend fun MessageWriteRepository.update(message: Message, transaction: Transaction) = updateAll(listOf(message), transaction)
suspend fun MessageWriteRepository.delete(message: Message, transaction: Transaction) = deleteAll(listOf(message), transaction)
suspend fun MessageWriteRepository.create(message: Message) = createAll(listOf(message))
suspend fun MessageWriteRepository.update(message: Message) = updateAll(listOf(message))
suspend fun MessageWriteRepository.delete(message: Message) = deleteAll(listOf(message))
