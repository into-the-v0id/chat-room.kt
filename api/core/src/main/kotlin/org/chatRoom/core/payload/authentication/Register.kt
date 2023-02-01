package org.chatRoom.core.payload.authentication

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Handle

@Serializable
data class Register(
    val email: EmailAddress,
    val handle: Handle,
)
