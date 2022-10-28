package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Message

interface MessageWriteRepository {
    fun createAll(messages: List<Message>)
    fun updateAll(messages: List<Message>)
    fun deleteAll(messages: List<Message>)
}

fun MessageWriteRepository.create(message: Message) = createAll(listOf(message))
fun MessageWriteRepository.update(message: Message) = updateAll(listOf(message))
fun MessageWriteRepository.delete(message: Message) = deleteAll(listOf(message))
