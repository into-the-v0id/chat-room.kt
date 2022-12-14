package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MessageWriteStateRepository(
    private val dataSource: DataSource,
) : MessageWriteRepository {
    private val tableName = "message_state"

    override suspend fun createAll(messages: Collection<Message>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        var statement = DSL.using(connection, SQLDialect.POSTGRES)
            .insertInto(
                DSL.table(tableName),
                listOf(
                    DSL.field("id"),
                    DSL.field("member_id"),
                    DSL.field("content"),
                    DSL.field("date_created"),
                    DSL.field("date_updated"),
                ),
            )

        messages.forEach { message ->
            statement = statement.values(listOf(
                message.modelId.toUuid(),
                message.memberId.toUuid(),
                message.content,
                message.dateCreated,
                message.dateUpdated,
            ))
        }

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != messages.size) error("Unable to insert all specified messages")
    }

    override suspend fun updateAll(messages: Collection<Message>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val valueRows = messages.map { message -> DSL.row(
            message.modelId.toUuid(),
            message.memberId.toUuid(),
            message.content,
            message.dateCreated,
            message.dateUpdated,
        ) }

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .update(DSL.table(tableName).`as`("old"))
            .set(mapOf(
                DSL.field("member_id") to DSL.field("new.member_id"),
                DSL.field("content") to DSL.field("new.content"),
                DSL.field("date_created") to DSL.field("new.date_created"),
                DSL.field("date_updated") to DSL.field("new.date_updated"),
            ))
            .from(
                DSL.values(*valueRows.toTypedArray())
                    .`as`("new", listOf("id", "member_id", "content", "date_created", "date_updated"))
            )
            .where(DSL.field("old.id").eq(DSL.field("new.id")))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != messages.size) error("Unable to update all specified messages")
    }

    override suspend fun deleteAll(messages: Collection<Message>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .delete(DSL.table(tableName))
            .where(DSL.field("id").`in`(*messages.map { message -> message.modelId.toUuid() }.toTypedArray()))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != messages.size) error("Unable to delete all specified messages")
    }
}
