package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.OrderBy
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

    override fun getById(id: Id): User? {
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
        handles: List<Handle>?,
        offset: Offset?,
        limit: Limit?,
        orderBy: OrderBy?,
        orderDirection: OrderDirection?,
    ): Collection<User> {
        val aggregates = dataSource.connection.use { connection ->
            val conditions = mutableListOf<Condition>()

            if (ids != null) {
                conditions.add(
                    DSL.field("id")
                        .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
                )
            }

            if (handles != null) {
                conditions.add(
                    DSL.field("handle")
                        .`in`(*handles.map { handle -> handle.toString() }.toTypedArray())
                )
            }

            val orderByField = when (orderBy) {
                OrderBy.DATE_CREATED, null -> DSL.field("date_created")
                OrderBy.DATE_UPDATED -> DSL.field("date_updated")
            }
            val order = when (orderDirection) {
                OrderDirection.ASC, null -> orderByField.asc()
                OrderDirection.DESC -> orderByField.desc()
            }

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

        return aggregates
    }
}
