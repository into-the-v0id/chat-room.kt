package org.chatRoom.core.payload.authentication

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.EmailAddress

@Serializable
data class Login(
    val email: EmailAddress,
)
