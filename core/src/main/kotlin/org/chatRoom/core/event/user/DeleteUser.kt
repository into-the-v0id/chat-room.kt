package org.chatRoom.core.event.user

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.DateSerializer
import org.chatRoom.core.valueObject.Id
import java.util.Date

@Serializable
data class DeleteUser(
    override val eventId: Id = Id(),
    override val modelId: Id,
    @Serializable(with = DateSerializer::class)
    override val dateIssued: Date = Date(),
) : UserEvent
