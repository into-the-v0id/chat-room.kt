package org.chatRoom.api.repository.write.event

import kotlinx.serialization.json.*
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.repository.write.MemberWriteRepository
import javax.sql.DataSource

class MemberWriteEventRepository(
    dataSource: DataSource,
) : WriteEventRepository<MemberEvent>(dataSource, "member_events"), MemberWriteRepository {
    override fun serializeEvent(event: MemberEvent): JsonElement {
        return when (event) {
            is CreateMember -> Json.encodeToJsonElement(event)
            is DeleteMember -> Json.encodeToJsonElement(event)
        }
    }

    override fun createAll(members: List<Member>) {
        val events = members.map { member -> member.events }.flatten()
        createAllEvents(events)
    }

    override fun updateAll(members: List<Member>) {
        val events = members.map { member -> member.events }.flatten()
        persistAllEvents(events)
    }

    override fun deleteAll(members: List<Member>) {
        val events = members.map { member -> DeleteMember(modelId = member.modelId) }
        createAllEvents(events)
    }
}
