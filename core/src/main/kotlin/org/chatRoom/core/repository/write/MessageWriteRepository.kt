package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Message

interface MessageWriteRepository {
    fun create(message: Message)
    fun update(message: Message)
    fun delete(message: Message)
}
