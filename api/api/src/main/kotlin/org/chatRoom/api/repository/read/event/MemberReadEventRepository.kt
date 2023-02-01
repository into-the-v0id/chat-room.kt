package org.chatRoom.api.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.repository.read.MemberQuery
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.valueObject.Id
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

    override fun getById(id: Id): Member? = getAll(MemberQuery(ids = listOf(id))).firstOrNull()

    override fun getAll(query: MemberQuery): Collection<Member> {
        val allEvents = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (query.ids != null) conditions.add(
                DSL.field("model_id")
                    .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
            )

            require(query.roomIds == null) { "Unsupported filter" }
            require(query.userIds == null) { "Unsupported filter" }
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
            .map { (_, events) -> Member.applyAllEvents(null, events) }
            .filterNotNull()
    }

    override fun count(query: MemberQuery): Int = getAll(query).size
}
