package org.chatRoom.core.repository

import org.chatRoom.core.aggreagte.Aggregate
import org.chatRoom.core.event.Event
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet

abstract class BaseRepository<A: Aggregate<E>, E: Event>(
    protected val connection: Connection,
    protected val tableName: String,
) {
    companion object {
        private fun prepareStatementWithEvent(
            statement: PreparedStatement,
            event: Event,
            positionOffset: Int = 0
        ) {
            statement.setString(positionOffset + 1, event.eventId.toString())
            statement.setString(positionOffset + 2, event.modelId.toString())
            statement.setString(positionOffset + 3, event::class.java.name)
            statement.setString(positionOffset + 4, "{}") // TODO: serialize event data
            statement.setDate(positionOffset + 5, Date(event.dateIssued.time))
        }
    }

    protected fun persistAllEvents(events: List<E>) {
        val sql = """
            INSERT INTO $tableName (event_id, model_id, event_type, event_data, date_issued)
            VALUES ${ "(?, ?, ?, ?, ?)".repeat(events.size).replace(")(", "), (") }
            ON CONFLICT (event_id) DO NOTHING
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        events.forEachIndexed { index, event -> prepareStatementWithEvent(statement, event, index * 5) }

        val modifiedRowCount = statement.executeUpdate()
        if (modifiedRowCount == 0) { /* do nothing */ }
    }

    protected fun insertEvent(event: E) {
        val sql = """
            INSERT INTO $tableName (event_id, model_id, event_type, event_data, date_issued)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        val statement = connection.prepareStatement(sql)
        prepareStatementWithEvent(statement, event)

        val modifiedRowCount = statement.executeUpdate()
        if (modifiedRowCount == 0) error("Unable to insert event")
    }

    protected fun parseEvent(resultSet: ResultSet): E {
        TODO("deserialize event")
    }

    protected fun parseAllEvents(resultSet: ResultSet): List<E> {
        val events = mutableListOf<E>()

        while (resultSet.next()) {
            val event = parseEvent(resultSet)
            events.add(event)
        }

        return events
    }
}
