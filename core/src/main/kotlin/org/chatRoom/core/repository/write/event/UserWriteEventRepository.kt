package org.chatRoom.core.repository.write.event

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

    override fun create(user: User) = createAllEvents(user.events)

    override fun update(user: User) = persistAllEvents(user.events)

    override fun delete(user: User) = createEvent(DeleteUser(modelId = user.modelId))
}
