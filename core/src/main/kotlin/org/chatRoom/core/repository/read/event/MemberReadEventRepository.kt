package org.chatRoom.core.repository.read.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.valueObject.Id
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MemberReadEventRepository(
    dataSource: DataSource,
) : ReadEventRepository<MemberEvent>(dataSource, "member_events"), MemberReadRepository {
    override fun deserializeEvent(type: String, data: JsonElement): MemberEvent {
        return when (type) {
            CreateMember::class.java.name -> Json.decodeFromJsonElement<CreateMember>(data)
            DeleteMember::class.java.name -> Json.decodeFromJsonElement<DeleteMember>(data)
            else -> error("Unknown event type")
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

    override fun getAll(ids: List<Id>?, userIds: List<Id>?, roomIds: List<Id>?): Collection<Member> {
        val allEvents = dataSource.connection.use { connection ->
            if (ids != null) error("Unsupported filter")
            if (roomIds != null) error("Unsupported filter")
            if (userIds != null) error("Unsupported filter")

            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .orderBy(DSL.field("date_issued").asc())

            val result = query.fetch()
            parseAllEvents(result)
        }

        return allEvents.groupBy { event -> event.modelId }
            .map { (_, events) -> Member.applyAllEvents(null, events) }
            .filterNotNull()
    }
}
