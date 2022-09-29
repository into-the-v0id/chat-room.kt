package org.chatRoom.events.user

import org.chatRoom.valueObject.Id
import java.util.Date

data class CreateUser(
    override val eventId: Id = Id(),
    override val modelId: Id,
    override val dateIssued: Date = Date(),
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
) : UserEvent
