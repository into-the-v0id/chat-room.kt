package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface MessageWriteRepository {
    fun createAll(messages: Collection<Message>, transaction: Transaction)
    fun updateAll(messages: Collection<Message>, transaction: Transaction)
    fun deleteAll(messages: Collection<Message>, transaction: Transaction)
}

fun MessageWriteRepository.createAll(messages: Collection<Message>) = Transaction(id = Id()).execute { transaction ->
    createAll(messages, transaction)
}
fun MessageWriteRepository.updateAll(messages: Collection<Message>) = Transaction(id = Id()).execute { transaction ->
    updateAll(messages, transaction)
}
fun MessageWriteRepository.deleteAll(messages: Collection<Message>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(messages, transaction)
}
fun MessageWriteRepository.create(message: Message, transaction: Transaction) = createAll(listOf(message), transaction)
fun MessageWriteRepository.update(message: Message, transaction: Transaction) = updateAll(listOf(message), transaction)
fun MessageWriteRepository.delete(message: Message, transaction: Transaction) = deleteAll(listOf(message), transaction)
fun MessageWriteRepository.create(message: Message) = createAll(listOf(message))
fun MessageWriteRepository.update(message: Message) = updateAll(listOf(message))
fun MessageWriteRepository.delete(message: Message) = deleteAll(listOf(message))
