package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.*
import org.chatRoom.core.repository.write.UserWriteRepository
import javax.sql.DataSource

class UserWriteEventRepository(
    dataSource: DataSource,
) : WriteEventRepository<UserEvent>(dataSource, "user_events"), UserWriteRepository {
    override fun serializeEvent(event: UserEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateUser -> CreateUser::class.java.name to Json.encodeToJsonElement(event)
            is ChangeHandle -> ChangeHandle::class.java.name to Json.encodeToJsonElement(event)
            is ChangeEmail -> ChangeEmail::class.java.name to Json.encodeToJsonElement(event)
            is DeleteUser -> DeleteUser::class.java.name to Json.encodeToJsonElement(event)
        }
    }

    override fun createAll(users: List<User>) {
        val events = users.map { user -> user.events }.flatten()
        createAllEvents(events)
    }

    override fun updateAll(users: List<User>) {
        val events = users.map { user -> user.events }.flatten()
        persistAllEvents(events)
    }

    override fun deleteAll(users: List<User>) {
        val events = users.map { user -> DeleteUser(modelId = user.modelId) }
        createAllEvents(events)
    }
}
