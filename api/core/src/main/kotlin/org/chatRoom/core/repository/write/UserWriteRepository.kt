package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface UserWriteRepository {
    suspend fun createAll(users: Collection<User>, transaction: Transaction)
    suspend fun updateAll(users: Collection<User>, transaction: Transaction)
    suspend fun deleteAll(users: Collection<User>, transaction: Transaction)
}

suspend fun UserWriteRepository.createAll(users: Collection<User>) = Transaction(id = Id()).execute { transaction ->
    createAll(users, transaction)
}
suspend fun UserWriteRepository.updateAll(users: Collection<User>) = Transaction(id = Id()).execute { transaction ->
    updateAll(users, transaction)
}
suspend fun UserWriteRepository.deleteAll(users: Collection<User>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(users, transaction)
}
suspend fun UserWriteRepository.create(user: User, transaction: Transaction) = createAll(listOf(user), transaction)
suspend fun UserWriteRepository.update(user: User, transaction: Transaction) = updateAll(listOf(user), transaction)
suspend fun UserWriteRepository.delete(user: User, transaction: Transaction) = deleteAll(listOf(user), transaction)
suspend fun UserWriteRepository.create(user: User) = createAll(listOf(user))
suspend fun UserWriteRepository.update(user: User) = updateAll(listOf(user))
suspend fun UserWriteRepository.delete(user: User) = deleteAll(listOf(user))
