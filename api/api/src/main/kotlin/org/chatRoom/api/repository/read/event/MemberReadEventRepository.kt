package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.member.MemberSortCriterion
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.lang.IllegalArgumentException
import javax.sql.DataSource

class MemberReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<MemberEvent>(dataSource, "member_events"), MemberReadRepository {
    override fun deserializeEvent(data: JsonElement): MemberEvent {
        return when (data.jsonObject["eventType"]?.jsonPrimitive?.content) {
            CreateMember.eventType -> Json.decodeFromJsonElement<CreateMember>(data)
            DeleteMember.eventType -> Json.decodeFromJsonElement<DeleteMember>(data)
            else -> throw IllegalArgumentException("Unknown event type")
        }
    }

    override fun getById(id: Id): Member? {
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

        return Member.applyAllEvents(null, events)
    }

    override fun getAll(
        ids: List<Id>?,
        userIds: List<Id>?,
        roomIds: List<Id>?,
        offset: Offset?,
        limit: Limit?,
        sortCriteria: List<MemberSortCriterion>,
    ): Collection<Member> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
            )

            if (roomIds != null) error("Unsupported filter")
            if (userIds != null) error("Unsupported filter")
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
            .map { (_, events) -> Member.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
