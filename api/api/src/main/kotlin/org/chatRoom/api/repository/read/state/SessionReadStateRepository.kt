package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.session.SessionSortCriterion
import org.jooq.Condition
import org.jooq.Record
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.SelectLimitPercentAfterOffsetStep
import org.jooq.SelectWhereStep
import org.jooq.impl.DSL
import java.time.Instant
import javax.sql.DataSource

class SessionReadStateRepository(
    private val dataSource: DataSource,
) : SessionReadRepository {
    private val tableName = "session_state"

    private fun parseAggregate(record: Record): Session = Session(
        modelId = record.get("id", Id::class.java)!!,
        userId = record.get("user_id", Id::class.java)!!,
        token = record.get("token", Token::class.java)!!,
        dateValidUntil = record.get("date_valid_until", Instant::class.java)!!,
        dateCreated = record.get("date_created", Instant::class.java)!!,
    )

    private fun parseAllAggregates(result: Result<Record>): List<Session> = result.map { record -> parseAggregate(record) }

    override fun getById(id: Id): Session? = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))
            .where(DSL.field("id").eq(id.toUuid()))
            .orderBy(DSL.field("date_created").asc())

        val result = fetch.fetch()
        parseAllAggregates(result).firstOrNull()
    }

    private fun <R: Record> applyQuery(
        fetch: SelectWhereStep<R>,
        query: SessionQuery,
    ): SelectLimitPercentAfterOffsetStep<R> {
        val conditions = mutableListOf<Condition>()

        if (query.ids != null) conditions.add(
            DSL.field("id")
                .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
        )

        if (query.userIds != null) conditions.add(
            DSL.field("user_id")
                .`in`(*query.userIds!!.map { id -> id.toString() }.toTypedArray())
        )

        if (query.tokens != null) conditions.add(
            DSL.field("token")
                .`in`(*query.tokens!!.map { token -> token.toString() }.toTypedArray())
        )

        val order = query.sortCriteria.map { criterion -> when (criterion) {
            SessionSortCriterion.DATE_VALID_UNTIL_ASC -> DSL.field("date_valid_until").asc()
            SessionSortCriterion.DATE_VALID_UNTIL_DESC -> DSL.field("date_valid_until").desc()
            SessionSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            SessionSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
        }}

        return fetch
            .where(conditions)
            .orderBy(order)
            .offset(query.offset?.toInt())
            .limit(query.limit?.toInt())
    }

    override fun getAll(query: SessionQuery): Collection<Session> = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetch()
        parseAllAggregates(result)
    }

    override fun count(query: SessionQuery): Int = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select(DSL.count().`as`("count"))
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetchOne()!!
        result.get("count", Int::class.java)
    }
}
