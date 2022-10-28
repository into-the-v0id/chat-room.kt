package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.User

interface UserWriteRepository {
    fun createAll(users: List<User>)
    fun updateAll(users: List<User>)
    fun deleteAll(users: List<User>)
}

fun UserWriteRepository.create(user: User) = createAll(listOf(user))
fun UserWriteRepository.update(user: User) = updateAll(listOf(user))
fun UserWriteRepository.delete(user: User) = deleteAll(listOf(user))
