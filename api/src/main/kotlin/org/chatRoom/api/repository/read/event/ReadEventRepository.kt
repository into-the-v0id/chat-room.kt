package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.event.Event
import org.jooq.Record
import org.jooq.Result
import java.time.Instant
import javax.sql.DataSource

abstract class ReadEventRepository<E: Event>(
    protected val dataSource: DataSource,
    protected val tableName: String,
) {
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
        dataMap["dateIssued"] = JsonPrimitive(dateIssued.toEpochMilli())

        data = JsonObject(dataMap)

        val eventType = record.get("event_type", String::class.java) ?: error("Expected event type")

        return deserializeEvent(eventType, data)
    }

    protected fun parseAllEvents(result: Result<Record>): List<E> = result.map { record -> parseEvent(record) }
}
