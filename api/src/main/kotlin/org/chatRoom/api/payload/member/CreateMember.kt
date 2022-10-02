package org.chatRoom.api.payload.member

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
data class CreateMember(
    val userId: Id,
    val roomId: Id,
)
