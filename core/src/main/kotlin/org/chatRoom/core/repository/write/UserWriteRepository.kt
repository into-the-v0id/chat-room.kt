package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.User

interface UserWriteRepository {
    fun create(user: User)
    fun update(user: User)
    fun delete(user: User)
}
