package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.*
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.UserWriteRepository
import javax.sql.DataSource

class UserWriteEventRepository(
    private val dataSource: DataSource,
) : WriteEventRepository<UserEvent>("user_events"), UserWriteRepository {
    override fun serializeEvent(event: UserEvent): JsonElement = when (event) {
        is CreateUser -> Json.encodeToJsonElement(event)
        is ChangeHandle -> Json.encodeToJsonElement(event)
        is ChangeEmail -> Json.encodeToJsonElement(event)
        is DeleteUser -> Json.encodeToJsonElement(event)
    }

    override fun createAll(users: Collection<User>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = users.map { user -> user.events }.flatten()
        createAllEvents(events, connection)
    }

    override fun updateAll(users: Collection<User>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = users.map { user -> user.events }.flatten()
        persistAllEvents(events, connection)
    }

    override fun deleteAll(users: Collection<User>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = users.map { user -> DeleteUser(modelId = user.modelId) }
        createAllEvents(events, connection)
    }
}
