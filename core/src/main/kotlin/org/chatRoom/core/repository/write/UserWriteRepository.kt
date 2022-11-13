package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface UserWriteRepository {
    fun createAll(users: Collection<User>, transaction: Transaction)
    fun updateAll(users: Collection<User>, transaction: Transaction)
    fun deleteAll(users: Collection<User>, transaction: Transaction)
}

fun UserWriteRepository.createAll(users: Collection<User>) = Transaction(id = Id()).execute { transaction ->
    createAll(users, transaction)
}
fun UserWriteRepository.updateAll(users: Collection<User>) = Transaction(id = Id()).execute { transaction ->
    updateAll(users, transaction)
}
fun UserWriteRepository.deleteAll(users: Collection<User>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(users, transaction)
}
fun UserWriteRepository.create(user: User, transaction: Transaction) = createAll(listOf(user), transaction)
fun UserWriteRepository.update(user: User, transaction: Transaction) = updateAll(listOf(user), transaction)
fun UserWriteRepository.delete(user: User, transaction: Transaction) = deleteAll(listOf(user), transaction)
fun UserWriteRepository.create(user: User) = createAll(listOf(user))
fun UserWriteRepository.update(user: User) = updateAll(listOf(user))
fun UserWriteRepository.delete(user: User) = deleteAll(listOf(user))
