package org.chatRoom.core.payload.member

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
data class CreateMember(
    val roomId: Id,
)
