package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.subscribeSqlConnection
import org.chatRoom.core.repository.write.MemberWriteRepository
import javax.sql.DataSource

class MemberWriteEventRepository(
    private val dataSource: DataSource,
) : WriteEventRepository<MemberEvent>("member_events"), MemberWriteRepository {
    override fun serializeEvent(event: MemberEvent): JsonElement = when (event) {
        is CreateMember -> Json.encodeToJsonElement(event)
        is DeleteMember -> Json.encodeToJsonElement(event)
    }

    override suspend fun createAll(members: Collection<Member>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = members.map { member -> member.events }.flatten()
        createAllEvents(events, connection)
    }

    override suspend fun updateAll(members: Collection<Member>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = members.map { member -> member.events }.flatten()
        persistAllEvents(events, connection)
    }

    override suspend fun deleteAll(members: Collection<Member>, transaction: Transaction) {
        val connection = dataSource.connection
        transaction.subscribeSqlConnection(connection)

        val events = members.map { member -> DeleteMember(modelId = member.modelId) }
        createAllEvents(events, connection)
    }
}
