package org.chatRoom.core.repository

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.DeleteUser
import org.chatRoom.core.event.user.UserEvent
import org.chatRoom.core.valueObject.Id
import java.sql.Connection

class UserRepository(connection: Connection) : BaseRepository<User, UserEvent>(connection, "user_events") {
    fun persist(user: User) = persistAllEvents(user.events)

    // TODO: check if user is already deleted
    fun delete(user: User) = insertEvent(DeleteUser(modelId = user.modelId))

    fun getById(id: Id): User? {
        val sql = """
            SELECT *
            FROM $tableName
            WHERE model_id = ?
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        statement.setString(1, id.toString())

        val resultSet = statement.executeQuery()
        val events = parseAllEvents(resultSet)
        if (events.isEmpty()) return null

        return User.applyAllEvents(null, events)
    }
}
