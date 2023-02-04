package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.UserQuery
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.UserSortCriterion
import org.jooq.Condition
import org.jooq.Record
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.SelectLimitPercentAfterOffsetStep
import org.jooq.SelectWhereStep
import org.jooq.impl.DSL
import java.time.Instant
import javax.sql.DataSource

class UserReadStateRepository(
    private val dataSource: DataSource,
) : UserReadRepository {
    private val tableName = "user_state"

    private fun parseAggregate(record: Record): User = User(
        modelId = record.get("id", Id::class.java)!!,
        handle = record.get("handle", Handle::class.java)!!,
        email = record.get("email", EmailAddress::class.java)!!,
        password = record.get("password", Password::class.java)!!,
        dateCreated = record.get("date_created", Instant::class.java)!!,
        dateUpdated = record.get("date_updated", Instant::class.java)!!,
    )

    private fun parseAllAggregates(result: Result<Record>): List<User> = result.map { record -> parseAggregate(record) }

    override fun getById(id: Id): User? = getAll(UserQuery(ids = listOf(id))).firstOrNull()

    private fun <R: Record> applyQuery(
        fetch: SelectWhereStep<R>,
        query: UserQuery,
    ): SelectLimitPercentAfterOffsetStep<R> {
        val conditions = mutableListOf<Condition>()

        if (query.ids != null) conditions.add(
            DSL.field("id")
                .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
        )

        if (query.handles != null) conditions.add(
            DSL.field("handle")
                .`in`(*query.handles!!.map { handle -> handle.toString() }.toTypedArray())
        )

        if (query.emails != null) conditions.add(
            DSL.field("email")
                .`in`(*query.emails!!.map { email -> email.toString() }.toTypedArray())
        )

        val order = query.sortCriteria.map { criterion -> when (criterion) {
            UserSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            UserSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
            UserSortCriterion.DATE_UPDATED_ASC -> DSL.field("date_updated").asc()
            UserSortCriterion.DATE_UPDATED_DESC -> DSL.field("date_updated").desc()
        }}

        return fetch
            .where(conditions)
            .orderBy(order)
            .offset(query.offset?.toInt())
            .limit(query.limit?.toInt())
    }

    override fun getAll(query: UserQuery): Collection<User> = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetch()
        parseAllAggregates(result)
    }

    override fun count(query: UserQuery): Int = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select(DSL.count().`as`("count"))
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetchOne()!!
        result.get("count", Int::class.java)
    }
}
