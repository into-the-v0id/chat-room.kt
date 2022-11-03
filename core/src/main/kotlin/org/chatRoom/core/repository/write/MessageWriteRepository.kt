package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Message

interface MessageWriteRepository {
    fun createAll(messages: Collection<Message>)
    fun updateAll(messages: Collection<Message>)
    fun deleteAll(messages: Collection<Message>)
}

fun MessageWriteRepository.create(message: Message) = createAll(listOf(message))
fun MessageWriteRepository.update(message: Message) = updateAll(listOf(message))
fun MessageWriteRepository.delete(message: Message) = deleteAll(listOf(message))
