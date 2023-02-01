package org.chatRoom.api.repository.write.guard

import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.repository.write.SessionWriteRepository

class SessionWriteGuardRepository(
    private val repository: SessionWriteRepository,
    private val sessionReadRepository: SessionReadRepository,
) : SessionWriteRepository {
    override suspend fun createAll(sessions: Collection<Session>, transaction: Transaction) {
        val sessionIds = sessions.map { session -> session.modelId }
        if (sessionReadRepository.getAll(SessionQuery(ids = sessionIds)).isNotEmpty()) error("Unable to create all specified sessions: Session already exists")

        repository.createAll(sessions, transaction)
    }

    override suspend fun deleteAll(sessions: Collection<Session>, transaction: Transaction) {
        val sessionIds = sessions.map { session -> session.modelId }
        val allIdsExist = sessionReadRepository.getAll(SessionQuery(ids = sessionIds))
            .map { session -> session.modelId }
            .containsAll(sessionIds)
        if (! allIdsExist) error("Unable to delete all specified sessions: Session not found")

        repository.deleteAll(sessions, transaction)
    }
}
