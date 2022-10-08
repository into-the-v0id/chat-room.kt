package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.event.Event
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

abstract class EventRepository<E: Event>(
    protected val dataSource: DataSource,
    protected val tableName: String,
) {
    protected abstract fun serializeEvent(event: E) : Pair<String, JsonElement>

    private fun prepareStatementWithEvent(
        statement: PreparedStatement,
        event: E,
        positionOffset: Int = 0
    ) {
        var (eventType, data) = serializeEvent(event)
        if (data !is JsonObject) error("Expected JSON object")

        val dataMap = mutableMapOf<String, JsonElement>()
        data.entries.forEach { (key, value) -> dataMap[key] = value }

        dataMap.remove("eventId")
        dataMap.remove("modelId")
        dataMap.remove("dateIssued")

        data = JsonObject(dataMap)

        statement.setString(positionOffset + 1, event.eventId.toString())
        statement.setString(positionOffset + 2, event.modelId.toString())
        statement.setString(positionOffset + 3, eventType)
        statement.setString(positionOffset + 4, data.toString())
        statement.setTimestamp(positionOffset + 5, Timestamp.from(event.dateIssued))
    }

    protected fun createEvent(event: E) = createAllEvents(listOf(event))

    protected fun createAllEvents(events: List<E>) {
        dataSource.connection.use { connection ->
            val sql = """
                INSERT INTO $tableName (event_id, model_id, event_type, event_data, date_issued)
                VALUES ${ "(?::uuid, ?::uuid, ?, ?::json, ?)".repeat(events.size).replace(")(", "), (") }
            """.trimIndent()
            val statement = connection.prepareStatement(sql)
            events.forEachIndexed { index, event -> prepareStatementWithEvent(statement, event, index * 5) }

            val modifiedRowCount = statement.executeUpdate()
            if (modifiedRowCount == 0) error("Unable to insert events")
        }
    }

    protected fun persistAllEvents(events: List<E>) {
        dataSource.connection.use { connection ->
            val sql = """
                INSERT INTO $tableName (event_id, model_id, event_type, event_data, date_issued)
                VALUES ${ "(?::uuid, ?::uuid, ?, ?::json, ?)".repeat(events.size).replace(")(", "), (") }
                ON CONFLICT (event_id) DO NOTHING
            """.trimIndent()
            val statement = connection.prepareStatement(sql)
            events.forEachIndexed { index, event -> prepareStatementWithEvent(statement, event, index * 5) }

            val modifiedRowCount = statement.executeUpdate()
            if (modifiedRowCount == 0) { /* do nothing */ }
        }
    }

    protected abstract fun deserializeEvent(type: String, data: JsonElement) : E

    protected fun parseEvent(resultSet: ResultSet): E {
        val rawData = resultSet.getString("event_data") ?: error("Expected event data")

        var data = Json.parseToJsonElement(rawData)
        if (data !is JsonObject) error("Expected JSON object")

        val dataMap = mutableMapOf<String, JsonElement>()
        data.entries.forEach { (key, value) -> dataMap[key] = value }

        val eventId = resultSet.getString("event_id") ?: error("Expected event ID")
        val modelId = resultSet.getString("model_id") ?: error("Expected model ID")
        val dateIssued = resultSet.getTimestamp("date_issued") ?: error("Expected event date")
        dataMap["eventId"] = JsonPrimitive(eventId)
        dataMap["modelId"] = JsonPrimitive(modelId)
        dataMap["dateIssued"] = JsonPrimitive(dateIssued.time)

        data = JsonObject(dataMap)

        val eventType = resultSet.getString("event_type") ?: error("Expected event type")

        return deserializeEvent(eventType, data)
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
