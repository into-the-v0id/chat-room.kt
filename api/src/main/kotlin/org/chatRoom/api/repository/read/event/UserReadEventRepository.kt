package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.*
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.UserSortCriterion
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
            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(DSL.field("model_id").eq(id.toUuid()))
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        if (events.isEmpty()) return null

        return User.applyAllEvents(null, events)
    }

    override fun getAll(
        ids: List<Id>?,
        handles: List<Handle>?,
        offset: Offset?,
        limit: Limit?,
        sortCriteria: List<UserSortCriterion>,
    ): Collection<User> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
            )

            if (handles != null) error("Unsupported filter")
            if (offset != null) error("Unsupported filter")
            if (limit != null) error("Unsupported filter")
            if (sortCriteria.isNotEmpty()) error("Custom sort criteria not supported")

            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(conditions)
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> User.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
