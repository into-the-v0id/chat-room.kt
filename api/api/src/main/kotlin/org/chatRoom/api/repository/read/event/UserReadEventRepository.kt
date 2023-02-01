package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.*
import org.chatRoom.core.repository.read.UserQuery
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.valueObject.*
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class UserReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<UserEvent>(dataSource, "user_events"), UserReadRepository {
    override fun deserializeEvent(data: JsonElement): UserEvent {
        return when (data.jsonObject["eventType"]?.jsonPrimitive?.content) {
            CreateUser.eventType -> Json.decodeFromJsonElement<CreateUser>(data)
            ChangeHandle.eventType -> Json.decodeFromJsonElement<ChangeHandle>(data)
            ChangeEmail.eventType -> Json.decodeFromJsonElement<ChangeEmail>(data)
            DeleteUser.eventType -> Json.decodeFromJsonElement<DeleteUser>(data)
            else -> throw IllegalArgumentException("Unknown event type")
        }
    }

    override fun getById(id: Id): User? {
        val events = dataSource.connection.use { connection ->
            val fetch = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(DSL.field("model_id").eq(id.toUuid()))
                .orderBy(DSL.field("date_issued").asc())

            val result = fetch.fetch()
            parseAllEvents(result)
        }

        if (events.isEmpty()) return null

        return User.applyAllEvents(null, events)
    }

    override fun getAll(query: UserQuery): Collection<User> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (query.ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
            )

            require(query.handles == null) { "Unsupported filter" }
            require(query.emails == null) { "Unsupported filter" }
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
            .map { (_, events) -> User.applyAllEvents(null, events) }
            .filterNotNull()
    }

    override fun count(query: UserQuery): Int = getAll(query).size
}
