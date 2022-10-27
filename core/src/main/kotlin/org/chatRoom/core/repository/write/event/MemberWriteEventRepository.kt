package org.chatRoom.core.repository.write.event

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
    override fun serializeEvent(event: MemberEvent): Pair<String, JsonElement> {
        return when (event) {
            is CreateMember -> CreateMember::class.java.name to Json.encodeToJsonElement(event)
            is DeleteMember -> DeleteMember::class.java.name to Json.encodeToJsonElement(event)
        }
    }

    override fun create(member: Member) = createAllEvents(member.events)

    override fun update(member: Member) = persistAllEvents(member.events)

    override fun delete(member: Member) = createEvent(DeleteMember(modelId = member.modelId))
}
