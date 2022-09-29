package org.chatRoom.events.user

import org.chatRoom.valueObject.Id
import java.util.Date

data class ChangeEmail(
    override val eventId: Id = Id(),
    override val modelId: Id,
    override val dateIssued: Date = Date(),
    val email: String,
) : UserEvent
