package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.read.MemberQuery
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.member.MemberSortCriterion
import org.jooq.*
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

    override fun getById(id: Id): Member? = getAll(MemberQuery(ids = listOf(id))).firstOrNull()

    private fun <R: Record> applyQuery(
        fetch: SelectWhereStep<R>,
        query: MemberQuery,
    ): SelectLimitPercentAfterOffsetStep<R> {
        val conditions = mutableListOf<Condition>()

        if (query.ids != null) conditions.add(
            DSL.field("id")
                .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
        )

        if (query.userIds != null) conditions.add(
            DSL.field("user_id")
                .`in`(*query.userIds!!.map { id -> id.toUuid() }.toTypedArray())
        )

        if (query.roomIds != null) conditions.add(
            DSL.field("room_id")
                .`in`(*query.roomIds!!.map { id -> id.toUuid() }.toTypedArray())
        )

        val order = query.sortCriteria.map { criterion -> when (criterion) {
            MemberSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            MemberSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
            MemberSortCriterion.DATE_UPDATED_ASC -> DSL.field("date_updated").asc()
            MemberSortCriterion.DATE_UPDATED_DESC -> DSL.field("date_updated").desc()
        }}

        return fetch
            .where(conditions)
            .orderBy(order)
            .offset(query.offset?.toInt())
            .limit(query.limit?.toInt())
    }

    override fun getAll(query: MemberQuery): Collection<Member> = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetch()
        parseAllAggregates(result)
    }

    override fun count(query: MemberQuery): Int = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select(DSL.count().`as`("count"))
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query.copy(sortCriteria = listOf())).fetchOne()!!
        result.get("count", Int::class.java)
    }
}
