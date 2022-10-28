package org.chatRoom.core.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator
import org.chatRoom.core.valueObject.Handle

@Serializable
data class CreateUser(
    val email: String,
    val handle: Handle,
) {
    init {
        if (! EmailValidator.getInstance().isValid(email)) error("Invalid email")
    }
}
