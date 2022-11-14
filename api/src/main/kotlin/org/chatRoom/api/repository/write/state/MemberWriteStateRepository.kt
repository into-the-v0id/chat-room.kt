package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MemberWriteStateRepository(
    private val dataSource: DataSource,
) : MemberWriteRepository {
    private val tableName = "member_state"

    override suspend fun createAll(members: Collection<Member>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        var statement = DSL.using(connection, SQLDialect.POSTGRES)
            .insertInto(
                DSL.table(tableName),
                listOf(
                    DSL.field("id"),
                    DSL.field("user_id"),
                    DSL.field("room_id"),
                    DSL.field("date_created"),
                    DSL.field("date_updated"),
                ),
            )

        members.forEach { member ->
            statement = statement.values(listOf(
                member.modelId.toUuid(),
                member.userId.toUuid(),
                member.roomId.toUuid(),
                member.dateCreated,
                member.dateUpdated,
            ))
        }

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != members.size) error("Unable to insert all specified members")
    }

    override suspend fun updateAll(members: Collection<Member>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val valueRows = members.map { member -> DSL.row(
            member.modelId.toUuid(),
            member.userId.toUuid(),
            member.roomId.toUuid(),
            member.dateCreated,
            member.dateUpdated,
        ) }

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .update(DSL.table(tableName).`as`("old"))
            .set(mapOf(
                DSL.field("user_id") to DSL.field("new.user_id"),
                DSL.field("room_id") to DSL.field("new.room_id"),
                DSL.field("date_created") to DSL.field("new.date_created"),
                DSL.field("date_updated") to DSL.field("new.date_updated"),
            ))
            .from(
                DSL.values(*valueRows.toTypedArray())
                    .`as`("new", listOf("id", "user_id", "room_id", "date_created", "date_updated"))
            )
            .where(DSL.field("old.id").eq(DSL.field("new.id")))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != members.size) error("Unable to update all specified members")
    }

    override suspend fun deleteAll(members: Collection<Member>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .delete(DSL.table(tableName))
            .where(DSL.field("id").`in`(*members.map { member -> member.modelId.toUuid() }.toTypedArray()))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != members.size) error("Unable to delete all specified members")
    }
}
