package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.*
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class UserReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<UserEvent>(dataSource, "user_events"), UserReadRepository {
    override fun deserializeEvent(type: String, data: JsonElement): UserEvent {
        return when (type) {
            CreateUser::class.java.name -> Json.decodeFromJsonElement<CreateUser>(data)
            ChangeHandle::class.java.name -> Json.decodeFromJsonElement<ChangeHandle>(data)
            ChangeEmail::class.java.name -> Json.decodeFromJsonElement<ChangeEmail>(data)
            DeleteUser::class.java.name -> Json.decodeFromJsonElement<DeleteUser>(data)
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

    override fun getAll(ids: List<Id>?, handles: List<Handle>?): List<User> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) {
                conditions.add(
                    DSL.field("model_id")
                        .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            if (handles != null) error("Unsupported filter")

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
