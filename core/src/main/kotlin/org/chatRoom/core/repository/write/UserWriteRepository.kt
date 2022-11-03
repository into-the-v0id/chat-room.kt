package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.User

interface UserWriteRepository {
    fun createAll(users: Collection<User>)
    fun updateAll(users: Collection<User>)
    fun deleteAll(users: Collection<User>)
}

fun UserWriteRepository.create(user: User) = createAll(listOf(user))
fun UserWriteRepository.update(user: User) = updateAll(listOf(user))
fun UserWriteRepository.delete(user: User) = deleteAll(listOf(user))
