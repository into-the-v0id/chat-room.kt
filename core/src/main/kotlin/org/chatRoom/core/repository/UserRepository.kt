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

    fun create(user: User) {
        if (getById(user.modelId) != null) error("Unable to create user: User already exists")
        // TODO: prevent duplicate handles

        persistAllEvents(user.events)
    }

    fun update(user: User) {
        if (getById(user.modelId) == null) error("Unable to update user: User not found")

        persistAllEvents(user.events)
    }

    fun delete(user: User) {
        if (getById(user.modelId) == null) error("Unable to delete user: User not found")

        insertEvent(DeleteUser(modelId = user.modelId))
    }

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

    fun getAll(): Collection<User> {
        val sql = """
            SELECT *
            FROM $tableName
            ORDER BY date_issued ASC
        """.trimIndent()
        val statement = connection.prepareStatement(sql)

        val resultSet = statement.executeQuery()
        val allEvents = parseAllEvents(resultSet)

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> User.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
