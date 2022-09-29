package org.chatRoom.events.user

import org.chatRoom.valueObject.Id
import java.util.Date

data class DeleteUser(
    override val eventId: Id = Id(),
    override val modelId: Id,
    override val dateIssued: Date = Date(),
) : UserEvent
