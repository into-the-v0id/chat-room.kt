package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.read.RoomQuery
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.room.RoomSortCriterion
import org.jooq.*
import org.jooq.impl.DSL
import java.time.Instant
import javax.sql.DataSource

class RoomReadStateRepository(
    private val dataSource: DataSource,
) : RoomReadRepository {
    private val tableName = "room_state"

    private fun parseAggregate(record: Record): Room = Room(
        modelId = record.get("id", Id::class.java)!!,
        handle = record.get("handle", Handle::class.java)!!,
        dateCreated = record.get("date_created", Instant::class.java)!!,
        dateUpdated = record.get("date_updated", Instant::class.java)!!,
    )

    private fun parseAllAggregates(result: Result<Record>): List<Room> = result.map { record -> parseAggregate(record) }

    override fun getById(id: Id): Room? = dataSource.connection.use { connection ->
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
        query: RoomQuery,
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

        val order = query.sortCriteria.map { criterion -> when (criterion) {
            RoomSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            RoomSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
            RoomSortCriterion.DATE_UPDATED_ASC -> DSL.field("date_updated").asc()
            RoomSortCriterion.DATE_UPDATED_DESC -> DSL.field("date_updated").desc()
        }}

        return fetch
            .where(conditions)
            .orderBy(order)
            .offset(query.offset?.toInt())
            .limit(query.limit?.toInt())
    }

    override fun getAll(query: RoomQuery): Collection<Room> = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetch()
        parseAllAggregates(result)
    }

    override fun count(query: RoomQuery): Int = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select(DSL.count().`as`("count"))
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetchOne()!!
        result.get("count", Int::class.java)
    }
}
