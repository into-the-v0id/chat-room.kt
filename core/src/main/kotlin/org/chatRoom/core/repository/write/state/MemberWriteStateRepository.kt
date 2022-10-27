package org.chatRoom.core.repository.write.state

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class MemberWriteStateRepository(
    private val dataSource: DataSource,
) : MemberWriteRepository {
    private val tableName = "member_state"

    override fun create(member: Member) {
        dataSource.connection.use { connection ->
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
                .values(listOf(
                    member.modelId.toUuid(),
                    member.userId.toUuid(),
                    member.roomId.toUuid(),
                    member.dateCreated,
                    member.dateUpdated,
                ))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to insert member")
        }
    }

    override fun update(member: Member) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .update(DSL.table(tableName))
                .set(DSL.field("user_id"), member.userId.toUuid())
                .set(DSL.field("room_id"), member.roomId.toUuid())
                .set(DSL.field("date_created"), member.dateCreated)
                .set(DSL.field("date_updated"), member.dateUpdated)
                .where(DSL.field("id").eq(member.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to update member")
        }
    }

    override fun delete(member: Member) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .delete(DSL.table(tableName))
                .where(DSL.field("id").eq(member.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to delete member")
        }
    }
}
