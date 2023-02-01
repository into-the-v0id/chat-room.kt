package org.chatRoom.api.repository.write.concurrent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.write.SessionWriteRepository

class SessionWriteConcurrentRepository(
    private val repositories: Collection<SessionWriteRepository>,
) : SessionWriteRepository {
    override suspend fun createAll(sessions: Collection<Session>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.createAll(sessions, transaction)
            }
        }
    }

    override suspend fun deleteAll(sessions: Collection<Session>, transaction: Transaction) = withContext(Dispatchers.Default) {
        repositories.forEach { repository ->
            launch {
                repository.deleteAll(sessions, transaction)
            }
        }
    }
}
