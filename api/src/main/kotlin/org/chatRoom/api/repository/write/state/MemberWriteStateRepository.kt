package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MemberWriteStateRepository(
    private val dataSource: DataSource,
) : MemberWriteRepository {
    private val tableName = "member_state"

    override fun createAll(members: Collection<Member>) = dataSource.connection.use { connection ->
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

    override fun updateAll(members: Collection<Member>) = dataSource.connection.use { connection ->
        val valueRows = members.map { member -> DSL.row(
            member.userId.toUuid(),
            member.roomId.toUuid(),
            member.dateCreated,
            member.dateUpdated,
        ) }

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .update(DSL.table(tableName).`as`("old"))
            .set(DSL.field("old.user_id"), "new.user_id")
            .set(DSL.field("old.room_id"), "new.room_id")
            .set(DSL.field("old.date_created"), "new.date_created")
            .set(DSL.field("old.date_updated"), "new.date_updated")
            .from(
                DSL.values(*valueRows.toTypedArray())
                    .`as`("new", listOf("user_id", "room_id", "date_created", "date_updated"))
            )
            .where(DSL.field("old.id").eq(DSL.field("new.id")))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != members.size) error("Unable to update all specified members")
    }

    override fun deleteAll(members: Collection<Member>) = dataSource.connection.use { connection ->
        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .delete(DSL.table(tableName))
            .where(DSL.field("id").`in`(*members.map { member -> member.modelId.toUuid() }.toTypedArray()))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != members.size) error("Unable to delete all specified members")
    }
}
