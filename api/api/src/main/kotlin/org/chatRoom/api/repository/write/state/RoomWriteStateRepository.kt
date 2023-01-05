package org.chatRoom.api.repository.write.state

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

class RoomWriteStateRepository(
    private val dataSource: DataSource,
) : RoomWriteRepository {
    private val tableName = "room_state"

    override suspend fun createAll(rooms: Collection<Room>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

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

        rooms.forEach { room ->
            statement = statement.values(listOf(
                room.modelId.toUuid(),
                room.handle.toString(),
                room.dateCreated,
                room.dateUpdated,
            ))
        }

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != rooms.size) error("Unable to insert all specified rooms")
    }

    override suspend fun updateAll(rooms: Collection<Room>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val valueRows = rooms.map { room -> DSL.row(
            room.modelId.toUuid(),
            room.handle.toString(),
            room.dateCreated,
            room.dateUpdated,
        ) }

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .update(DSL.table(tableName).`as`("old"))
            .set(mapOf(
                DSL.field("handle") to DSL.field("new.handle"),
                DSL.field("date_created") to DSL.field("new.date_created"),
                DSL.field("date_updated") to DSL.field("new.date_updated"),
            ))
            .from(
                DSL.values(*valueRows.toTypedArray())
                    .`as`("new", listOf("id", "handle", "date_created", "date_updated"))
            )
            .where(DSL.field("old.id").eq(DSL.field("new.id")))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != rooms.size) error("Unable to update all specified rooms")
    }

    override suspend fun deleteAll(rooms: Collection<Room>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val statement = DSL.using(connection, SQLDialect.POSTGRES)
            .delete(DSL.table(tableName))
            .where(DSL.field("id").`in`(*rooms.map { room -> room.modelId.toUuid() }.toTypedArray()))

        val modifiedRowCount = statement.execute()
        if (modifiedRowCount != rooms.size) error("Unable to delete all specified rooms")
    }
}
