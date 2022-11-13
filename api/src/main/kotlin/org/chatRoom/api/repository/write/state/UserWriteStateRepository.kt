package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.UserWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class UserWriteStateRepository(
    private val dataSource: DataSource,
) : UserWriteRepository {
    private val tableName = "user_state"

    override fun createAll(users: Collection<User>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        var statement = DSL.using(connection, SQLDialect.POSTGRES)
            .insertInto(
                DSL.table(tableName),
                listOf(
                    DSL.field("id"),
                    DSL.field("handle"),
                    DSL.field("email"),
                    DSL.field("date_created"),
                    DSL.field("date_updated"),
                ),
            )

        users.forEach { user ->
            statement = statement.values(listOf(
                user.modelId.toUuid(),
                user.handle.toString(),
                user.email,
                user.dateCreated,
                user.dateUpdated,
            ))
        }

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != users.size) error("Unable to insert all specified users")
    }

    override fun updateAll(users: Collection<User>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val valueRows = users.map { user -> DSL.row(
            user.handle.toString(),
            user.email,
            user.dateCreated,
            user.dateUpdated,
        ) }

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .update(DSL.table(tableName).`as`("old"))
            .set(DSL.field("old.handle"), "new.handle")
            .set(DSL.field("old.email"), "new.email")
            .set(DSL.field("old.date_created"), "new.date_created")
            .set(DSL.field("old.date_updated"), "new.date_updated")
            .from(
                DSL.values(*valueRows.toTypedArray())
                    .`as`("new", listOf("handle", "email", "date_created", "date_updated"))
            )
            .where(DSL.field("old.id").eq(DSL.field("new.id")))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != users.size) error("Unable to update all specified users")
    }

    override fun deleteAll(users: Collection<User>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .delete(DSL.table(tableName))
            .where(DSL.field("id").`in`(*users.map { user -> user.modelId.toUuid() }.toTypedArray()))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != users.size) error("Unable to delete all specified users")
    }
}
