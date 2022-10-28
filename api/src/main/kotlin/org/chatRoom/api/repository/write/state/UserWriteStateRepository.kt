package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.repository.write.UserWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class UserWriteStateRepository(
    private val dataSource: DataSource,
) : UserWriteRepository {
    private val tableName = "user_state"

    override fun create(user: User) {
        dataSource.connection.use { connection ->
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
                .values(listOf(
                    user.modelId.toUuid(),
                    user.handle.toString(),
                    user.email,
                    user.dateCreated,
                    user.dateUpdated,
                ))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to insert user")
        }
    }

    override fun update(user: User) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .update(DSL.table(tableName))
                .set(DSL.field("handle"), user.handle.toString())
                .set(DSL.field("email"), user.email)
                .set(DSL.field("date_created"), user.dateCreated)
                .set(DSL.field("date_updated"), user.dateUpdated)
                .where(DSL.field("id").eq(user.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to update user")
        }
    }

    override fun delete(user: User) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .delete(DSL.table(tableName))
                .where(DSL.field("id").eq(user.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to delete user")
        }
    }
}
