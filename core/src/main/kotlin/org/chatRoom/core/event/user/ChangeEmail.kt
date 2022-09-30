package org.chatRoom.core.event.user

import org.chatRoom.core.valueObject.Id
import java.util.Date

data class ChangeEmail(
    override val eventId: Id = Id(),
    override val modelId: Id,
    override val dateIssued: Date = Date(),
    val email: String,
) : UserEvent
