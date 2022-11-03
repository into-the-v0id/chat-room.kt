package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.jooq.Condition
import org.jooq.Record
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.time.Instant
import javax.sql.DataSource

class MemberReadStateRepository(
    private val dataSource: DataSource,
) : MemberReadRepository {
    private val tableName = "member_state"

    private fun parseAggregate(record: Record): Member = Member(
        modelId = record.get("id", Id::class.java)!!,
        userId = record.get("user_id", Id::class.java)!!,
        roomId = record.get("room_id", Id::class.java)!!,
        dateCreated = record.get("date_created", Instant::class.java)!!,
        dateUpdated = record.get("date_updated", Instant::class.java)!!,
    )

    private fun parseAllAggregates(result: Result<Record>): List<Member> = result.map { record -> parseAggregate(record) }

    override fun getById(id: Id): Member? {
        val aggregates = dataSource.connection.use { connection ->
            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(DSL.field("id").eq(id.toUuid()))
                .orderBy(DSL.field("date_created").asc())

            val result = query.fetch()
            parseAllAggregates(result)
        }

        return aggregates.firstOrNull()
    }

    override fun getAll(
        ids: List<Id>?,
        userIds: List<Id>?,
        roomIds: List<Id>?,
        offset: Offset?,
        limit: Limit?,
    ): Collection<Member> {
        val aggregates = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) {
                conditions.add(
                    DSL.field("id")
                        .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            if (userIds != null) {
                conditions.add(
                    DSL.field("user_id")
                        .`in`(*userIds.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            if (roomIds != null) {
                conditions.add(
                    DSL.field("room_id")
                        .`in`(*roomIds.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            val query = DSL.using(connection, SQLDialect.POSTGRES)
                .select()
                .from(DSL.table(tableName))
                .where(conditions)
                .orderBy(DSL.field("date_created").asc())
                .offset(offset?.toInt())
                .limit(limit?.toInt())

            val result = query.fetch()
            parseAllAggregates(result)
        }

        return aggregates
    }
}
