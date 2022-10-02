package org.chatRoom.api.payload.message

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
data class CreateMessage(
    val memberId: Id,
    val content: String,
)
