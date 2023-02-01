package org.chatRoom.core.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Handle

@Serializable
data class CreateUser(
    val email: EmailAddress,
    val handle: Handle,
)
