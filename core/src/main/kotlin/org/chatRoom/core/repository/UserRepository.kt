package org.chatRoom.core.repository

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.event.user.*
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class UserRepository(
    dataSource: DataSource,
    private val memberRepository: MemberRepository,
) : EventRepository<UserEvent>(dataSource, "user_events") {
    override fun serializeEvent(event: UserEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateUser -> CreateUser::class.java.name to Json.encodeToJsonElement(event)
            is ChangeHandle -> ChangeHandle::class.java.name to Json.encodeToJsonElement(event)
            is ChangeEmail -> ChangeEmail::class.java.name to Json.encodeToJsonElement(event)
            is DeleteUser -> DeleteUser::class.java.name to Json.encodeToJsonElement(event)
            else -> error("Unknown event")
        }
    }

    override fun deserializeEvent(type: String, data: JsonElement): UserEvent {
        return when (type) {
            CreateUser::class.java.name -> Json.decodeFromJsonElement<CreateUser>(data)
            ChangeHandle::class.java.name -> Json.decodeFromJsonElement<ChangeHandle>(data)
            ChangeEmail::class.java.name -> Json.decodeFromJsonElement<ChangeEmail>(data)
            DeleteUser::class.java.name -> Json.decodeFromJsonElement<DeleteUser>(data)
            else -> error("Unknown event type")
        }
    }

    fun create(user: User) {
        if (getById(user.modelId) != null) error("Unable to create user: User already exists")
        if (getAll(handles = listOf(user.handle)).isNotEmpty()) error("Unable to create user: Handle already exists")

        createAllEvents(user.events)
    }

    fun update(user: User) {
        if (getById(user.modelId) == null) error("Unable to update user: User not found")

        persistAllEvents(user.events)
    }

    fun delete(user: User) {
        if (getById(user.modelId) == null) error("Unable to delete user: User not found")

        val members = memberRepository.getAll(userIds = listOf(user.modelId))
        members.forEach { member -> memberRepository.delete(member) }

        createEvent(DeleteUser(modelId = user.modelId))
    }

    fun getById(id: Id): User? {
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

    fun getAll(ids: List<Id>? = null, handles: List<Handle>? = null): Collection<User> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) {
                conditions.add(
                    DSL.field("model_id")
                        .eq(DSL.any(*ids.map { id -> id.toUuid() }.toTypedArray()))
                )
            }

            if (handles != null) {
                val subquery = DSL.select(DSL.field("model_id"))
                    .from(DSL.table(tableName))
                    .where(DSL.or(
                        DSL.and(
                            DSL.field("event_type").eq(CreateUser::class.java.name),
                            DSL.condition(
                                "event_data->>'handle' = ANY(?)",
                                handles.map { handle -> handle.toString() }.toTypedArray(),
                            )
                        ),
                        DSL.and(
                            DSL.field("event_type").eq(ChangeHandle::class.java.name),
                            DSL.condition(
                                "event_data->>'handle' = ANY(?)",
                                handles.map { handle -> handle.toString() }.toTypedArray(),
                            )
                        ),
                    ))

                conditions.add(DSL.field("model_id").`in`(subquery))
            }

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
            .filter { member ->
                if (handles != null && member.handle !in handles) {
                    return@filter false
                }

                return@filter true
            }
    }
}
