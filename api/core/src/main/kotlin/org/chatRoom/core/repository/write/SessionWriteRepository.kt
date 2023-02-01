package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface SessionWriteRepository {
    suspend fun createAll(sessions: Collection<Session>, transaction: Transaction)
    suspend fun deleteAll(sessions: Collection<Session>, transaction: Transaction)
}

suspend fun SessionWriteRepository.createAll(sessions: Collection<Session>) = Transaction(id = Id()).execute { transaction ->
    createAll(sessions, transaction)
}
suspend fun SessionWriteRepository.deleteAll(sessions: Collection<Session>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(sessions, transaction)
}
suspend fun SessionWriteRepository.create(session: Session, transaction: Transaction) = createAll(listOf(session), transaction)
suspend fun SessionWriteRepository.delete(session: Session, transaction: Transaction) = deleteAll(listOf(session), transaction)
suspend fun SessionWriteRepository.create(session: Session) = createAll(listOf(session))
suspend fun SessionWriteRepository.delete(session: Session) = deleteAll(listOf(session))
