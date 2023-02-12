package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.event.session.*
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.valueObject.*
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class SessionReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<SessionEvent>(dataSource, "session_events"), SessionReadRepository {
    override fun deserializeEvent(data: JsonElement): SessionEvent {
        return when (data.jsonObject["eventType"]?.jsonPrimitive?.content) {
            CreateSession.eventType -> Json.decodeFromJsonElement<CreateSession>(data)
            DeleteSession.eventType -> Json.decodeFromJsonElement<DeleteSession>(data)
            else -> throw IllegalArgumentException("Unknown event type")
        }
    }

    override fun getById(id: Id): Session? = getAll(SessionQuery(ids = listOf(id))).firstOrNull()

    override fun getAll(query: SessionQuery): Collection<Session> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (query.ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
            )

            require(query.userIds == null) { "Unsupported filter" }
            require(query.isExpired == null) { "Unsupported filter" }
            require(query.offset == null) { "Unsupported filter" }
            require(query.limit == null) { "Unsupported filter" }
            require(query.sortCriteria.isEmpty()) { "Custom sort criteria not supported" }

            val fetch = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(conditions)
                .orderBy(DSL.field("date_issued").asc())

            val result = fetch.fetch()
            parseAllEvents(result)
        }

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Session.applyAllEvents(null, events) }
            .filterNotNull()
    }

    override fun count(query: SessionQuery): Int = getAll(query).size
}
