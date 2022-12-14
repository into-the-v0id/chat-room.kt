package org.chatRoom.api.repository.read.state

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.message.MessageSortCriterion
import org.jooq.Condition
import org.jooq.Record
import org.jooq.Result
import org.jooq.SQLDialect
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

    override fun getById(id: Id): Message? = dataSource.connection.use { connection ->
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
        memberIds: List<Id>?,
        offset: Offset?,
        limit: Limit?,
        sortCriteria: List<MessageSortCriterion>,
    ): Collection<Message> = dataSource.connection.use { connection ->
        val conditions = mutableListOf<Condition>()

        if (ids != null) conditions.add(
            DSL.field("id")
                .`in`(*ids.map { id -> id.toUuid() }.toTypedArray())
        )

        if (memberIds != null) conditions.add(
            DSL.field("member_id")
                .`in`(*memberIds.map { id -> id.toUuid() }.toTypedArray())
        )

        val order = sortCriteria.map { criterion -> when (criterion) {
            MessageSortCriterion.DATE_CREATED_ASC -> DSL.field("date_created").asc()
            MessageSortCriterion.DATE_CREATED_DESC -> DSL.field("date_created").desc()
            MessageSortCriterion.DATE_UPDATED_ASC -> DSL.field("date_updated").asc()
            MessageSortCriterion.DATE_UPDATED_DESC -> DSL.field("date_updated").desc()
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
