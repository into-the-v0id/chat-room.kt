package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.SessionWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class SessionWriteStateRepository(
    private val dataSource: DataSource,
) : SessionWriteRepository {
    private val tableName = "session_state"

    override suspend fun createAll(sessions: Collection<Session>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        var statement = DSL.using(connection, SQLDialect.POSTGRES)
            .insertInto(
                DSL.table(tableName),
                listOf(
                    DSL.field("id"),
                    DSL.field("user_id"),
                    DSL.field("secret_hash"),
                    DSL.field("date_valid_until"),
                    DSL.field("date_created"),
                ),
            )

        sessions.forEach { session ->
            statement = statement.values(listOf(
                session.modelId.toUuid(),
                session.userId.toUuid(),
                session.secretHash.toString(),
                session.dateValidUntil,
                session.dateCreated,
            ))
        }

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != sessions.size) error("Unable to insert all specified sessions")
    }

    override suspend fun deleteAll(sessions: Collection<Session>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .delete(DSL.table(tableName))
            .where(DSL.field("id").`in`(*sessions.map { session -> session.modelId.toUuid() }.toTypedArray()))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != sessions.size) error("Unable to delete all specified sessions")
    }
}
