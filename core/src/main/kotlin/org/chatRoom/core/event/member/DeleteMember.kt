package org.chatRoom.core.event.member

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.InstantSerializer
import org.chatRoom.core.valueObject.Id
import java.time.Instant

@Serializable
data class DeleteMember(
    override val eventId: Id = Id(),
    override val modelId: Id,
    @Serializable(with = InstantSerializer::class)
    override val dateIssued: Instant = Instant.now(),
) : MemberEvent