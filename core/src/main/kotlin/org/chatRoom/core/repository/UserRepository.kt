package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.ChangeEmail
import org.chatRoom.core.event.user.CreateUser
import org.chatRoom.core.event.user.DeleteUser
import org.chatRoom.core.event.user.UserEvent
import org.chatRoom.core.valueObject.Id
import java.sql.Connection

class UserRepository(connection: Connection) : EventRepository<UserEvent>(connection, "user_events") {
    override fun serializeEvent(event: UserEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateUser -> CreateUser::class.java.name to Json.encodeToJsonElement(event)
            is ChangeEmail -> ChangeEmail::class.java.name to Json.encodeToJsonElement(event)
            is DeleteUser -> DeleteUser::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): UserEvent {
        return when (type) {
            CreateUser::class.java.name -> Json.decodeFromJsonElement<CreateUser>(data)
            ChangeEmail::class.java.name -> Json.decodeFromJsonElement<ChangeEmail>(data)
            DeleteUser::class.java.name -> Json.decodeFromJsonElement<DeleteUser>(data)
            else -> error("Unknown event type")
        }
    }

    fun persist(user: User) = persistAllEvents(user.events)

    // TODO: check if user is already deleted
    fun delete(user: User) = insertEvent(DeleteUser(modelId = user.modelId))

    fun getById(id: Id): User? {
        val sql = """
            SELECT *
            FROM $tableName
            WHERE model_id = ?::uuid
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        statement.setString(1, id.toString())

        val resultSet = statement.executeQuery()
        val events = parseAllEvents(resultSet)
        if (events.isEmpty()) return null

        return User.applyAllEvents(null, events)
    }
}
