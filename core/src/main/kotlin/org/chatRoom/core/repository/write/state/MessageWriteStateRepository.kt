package org.chatRoom.core.repository.write.state

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MessageWriteStateRepository(
    private val dataSource: DataSource,
) : MessageWriteRepository {
    private val tableName = "message_state"

    override fun create(message: Message) {
        dataSource.connection.use { connection ->
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
                .values(listOf(
                    message.modelId.toUuid(),
                    message.memberId.toUuid(),
                    message.content,
                    message.dateCreated,
                    message.dateUpdated,
                ))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to insert message")
        }
    }

    override fun update(message: Message) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .update(DSL.table(tableName))
                .set(DSL.field("member_id"), message.memberId.toUuid())
                .set(DSL.field("content"), message.content)
                .set(DSL.field("date_created"), message.dateCreated)
                .set(DSL.field("date_updated"), message.dateUpdated)
                .where(DSL.field("id").eq(message.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to update message")
        }
    }

    override fun delete(message: Message) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .delete(DSL.table(tableName))
                .where(DSL.field("id").eq(message.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to delete message")
        }
    }
}
