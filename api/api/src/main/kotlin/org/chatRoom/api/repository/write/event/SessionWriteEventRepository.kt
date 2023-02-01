package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.event.session.*
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.SessionWriteRepository
import javax.sql.DataSource

class SessionWriteEventRepository(
    private val dataSource: DataSource,
) : WriteEventRepository<SessionEvent>("session_events"), SessionWriteRepository {
    override fun serializeEvent(event: SessionEvent): JsonElement = when (event) {
        is CreateSession -> Json.encodeToJsonElement(event)
        is DeleteSession -> Json.encodeToJsonElement(event)
    }

    override suspend fun createAll(sessions: Collection<Session>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = sessions.map { session -> session.events }.flatten()
        createAllEvents(events, connection)
    }

    override suspend fun deleteAll(sessions: Collection<Session>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = sessions.map { session -> DeleteSession(modelId = session.modelId) }
        createAllEvents(events, connection)
    }
}
