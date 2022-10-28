package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class RoomWriteStateRepository(
    private val dataSource: DataSource,
) : RoomWriteRepository {
    private val tableName = "room_state"

    override fun create(room: Room) {
        dataSource.connection.use { connection ->
            var statement = DSL.using(connection, SQLDialect.POSTGRES)
                .insertInto(
                    DSL.table(tableName),
                    listOf(
                        DSL.field("id"),
                        DSL.field("handle"),
                        DSL.field("date_created"),
                        DSL.field("date_updated"),
                    ),
                )
                .values(listOf(
                    room.modelId.toUuid(),
                    room.handle.toString(),
                    room.dateCreated,
                    room.dateUpdated,
                ))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to insert room")
        }
    }

    override fun update(room: Room) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .update(DSL.table(tableName))
                .set(DSL.field("handle"), room.handle.toString())
                .set(DSL.field("date_created"), room.dateCreated)
                .set(DSL.field("date_updated"), room.dateUpdated)
                .where(DSL.field("id").eq(room.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to update room")
        }
    }

    override fun delete(room: Room) {
        dataSource.connection.use { connection ->
            val statement = DSL.using(connection, SQLDialect.POSTGRES)
                .delete(DSL.table(tableName))
                .where(DSL.field("id").eq(room.modelId.toUuid()))

            val modifiedRowCount = statement.execute()
            if (modifiedRowCount == 0) error("Unable to delete room")
        }
    }
}
