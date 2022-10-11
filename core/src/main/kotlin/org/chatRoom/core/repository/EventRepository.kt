package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.event.Event
import org.jooq.InsertValuesStepN
import org.jooq.JSON
import org.jooq.Record
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.time.Instant
import javax.sql.DataSource

abstract class EventRepository<E: Event>(
    protected val dataSource: DataSource,
    protected val tableName: String,
) {
    protected abstract fun serializeEvent(event: E) : Pair<String, JsonElement>

    private fun prepareStatementWithEvent(
        statement: InsertValuesStepN<Record>,
        event: E,
    ): InsertValuesStepN<Record> {
        var (eventType, data) = serializeEvent(event)
        if (data !is JsonObject) error("Expected JSON object")

        val dataMap = mutableMapOf<String, JsonElement>()
        data.entries.forEach { (key, value) -> dataMap[key] = value }

        dataMap.remove("eventId")
        dataMap.remove("modelId")
        dataMap.remove("dateIssued")

        data = JsonObject(dataMap)

        return statement.values(listOf(
            event.eventId.toUuid(),
            event.modelId.toUuid(),
            eventType,
            JSON.valueOf(data.toString()),
            event.dateIssued,
        ))
    }

    protected fun createEvent(event: E) = createAllEvents(listOf(event))

    protected fun createAllEvents(events: List<E>) {
        dataSource.connection.use { connection ->
            var statement = DSL.using(connection, SQLDialect.POSTGRES)
                .insertInto(
                    DSL.table(tableName),
                    listOf(
                        DSL.field("event_id"),
                        DSL.field("model_id"),
                        DSL.field("event_type"),
                        DSL.field("event_data"),
                        DSL.field("date_issued"),
                    ),
                )

            events.forEach { event -> statement = prepareStatementWithEvent(statement, event) }

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to insert events")
        }
    }

    protected fun persistAllEvents(events: List<E>) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .insertInto(
                    DSL.table(tableName),
                    listOf(
                        DSL.field("event_id"),
                        DSL.field("model_id"),
                        DSL.field("event_type"),
                        DSL.field("event_data"),
                        DSL.field("date_issued"),
                    ),
                )
                .run {
                    var statement = this

                    events.forEach { event -> statement = prepareStatementWithEvent(statement, event) }

                    statement
                }
                .onConflict(DSL.field("event_id"))
                .doNothing()

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) { /* do nothing */ }
        }
    }

    protected abstract fun deserializeEvent(type: String, data: JsonElement) : E

    protected fun parseEvent(record: Record): E {
        val rawData = record.get("event_data", String::class.java) ?: error("Expected event data")

        var data = Json.parseToJsonElement(rawData)
        if (data !is JsonObject) error("Expected JSON object")

        val dataMap = mutableMapOf<String, JsonElement>()
        data.entries.forEach { (key, value) -> dataMap[key] = value }

        val eventId = record.get("event_id", String::class.java) ?: error("Expected event ID")
        val modelId = record.get("model_id", String::class.java) ?: error("Expected model ID")
        val dateIssued = record.get("date_issued", Instant::class.java) ?: error("Expected event date")
        dataMap["eventId"] = JsonPrimitive(eventId)
        dataMap["modelId"] = JsonPrimitive(modelId)
        dataMap["dateIssued"] = JsonPrimitive(dateIssued.nano)

        data = JsonObject(dataMap)

        val eventType = record.get("event_type", String::class.java) ?: error("Expected event type")

        return deserializeEvent(eventType, data)
    }

    protected fun parseAllEvents(result: Result<Record>): List<E> = result.map { record -> parseEvent(record) }
}
