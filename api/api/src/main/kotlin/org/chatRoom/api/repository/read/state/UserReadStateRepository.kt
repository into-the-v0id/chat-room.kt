package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.UserSortCriterion
import org.jooq.Condition
import org.jooq.Record
import org.jooq.Result
import org.jooq.SQLDialect
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
        email = record.get("email", String::class.java)!!,
        dateCreated = record.get("date_created", Instant::class.java)!!,
        dateUpdated = record.get("date_updated", Instant::class.java)!!,
    )

    private fun parseAllAggregates(result: Result<Record>): List<User> = result.map { record -> parseAggregate(record) }

    override fun getById(id: Id): User? = dataSource.connection.use { connection ->
        val query = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))
            .where(DSL.field("id").eq(id.toUuid()))
            .orderBy(DSL.field("date_created").asc())

        val result = query.fetch()
        parseAllAggregates(result).firstOrNull()
    }

    override fun getAll(
        ids: List<Id>?,
        handles: List<Handle>?,
        offset: Offset?,
        limit: Limit?,
        sortCriteria: List<UserSortCriterion>,
    ): Collection<User> = dataSource.connection.use { connection ->
        val conditions = mutableListOf<Condition>()

        if (ids != null) conditions.add(
            DSL.field("id")
                .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
        )

        if (handles != null) conditions.add(
            DSL.field("handle")
                .`in`(*handles.map { handle -> handle.toString() }.toTypedArray())
        )

        val order = sortCriteria.map { criterion -> when (criterion) {
            UserSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            UserSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
            UserSortCriterion.DATE_UPDATED_ASC -> DSL.field("date_updated").asc()
            UserSortCriterion.DATE_UPDATED_DESC -> DSL.field("date_updated").desc()
        }}

        val query = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))
            .where(conditions)
            .orderBy(order)
            .offset(offset?.toInt())
            .limit(limit?.toInt())

        val result = query.fetch()
        parseAllAggregates(result)
    }
}
