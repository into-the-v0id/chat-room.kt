package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.read.MessageQuery
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.message.MessageSortCriterion
import org.jooq.*
import org.jooq.impl.DSL
import java.time.Instant
import javax.sql.DataSource

class MessageReadStateRepository(
    private val dataSource: DataSource,
) : MessageReadRepository {
    private val tableName = "message_state"

    private fun parseAggregate(record: Record): Message = Message(
        modelId = record.get("id", Id::class.java)!!,
        memberId = record.get("member_id", Id::class.java)!!,
        content = record.get("content", String::class.java)!!,
        dateCreated = record.get("date_created", Instant::class.java)!!,
        dateUpdated = record.get("date_updated", Instant::class.java)!!,
    )

    private fun parseAllAggregates(result: Result<Record>): List<Message> = result.map { record -> parseAggregate(record) }

    override fun getById(id: Id): Message? = getAll(MessageQuery(ids = listOf(id))).firstOrNull()

    private fun <R: Record> applyQuery(
        fetch: SelectWhereStep<R>,
        query: MessageQuery,
    ): SelectLimitPercentAfterOffsetStep<R> {
        val conditions = mutableListOf<Condition>()

        if (query.ids != null) conditions.add(
            DSL.field("id")
                .`in`(*query.ids!!.map { id -> id.toUuid() }.toTypedArray())
        )

        if (query.memberIds != null) conditions.add(
            DSL.field("member_id")
                .`in`(*query.memberIds!!.map { id -> id.toUuid() }.toTypedArray())
        )

        val order = query.sortCriteria.map { criterion -> when (criterion) {
            MessageSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            MessageSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
            MessageSortCriterion.DATE_UPDATED_ASC -> DSL.field("date_updated").asc()
            MessageSortCriterion.DATE_UPDATED_DESC -> DSL.field("date_updated").desc()
        }}

        return fetch
            .where(conditions)
            .orderBy(order)
            .offset(query.offset?.toInt())
            .limit(query.limit?.toInt())
    }

    override fun getAll(query: MessageQuery): Collection<Message> = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select()
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetch()
        parseAllAggregates(result)
    }

    override fun count(query: MessageQuery): Int = dataSource.connection.use { connection ->
        val fetch = DSL.using(connection, SQLDialect.POSTGRES)
            .select(DSL.count().`as`("count"))
            .from(DSL.table(tableName))

        val result = applyQuery(fetch, query).fetchOne()!!
        result.get("count", Int::class.java)
    }
}
