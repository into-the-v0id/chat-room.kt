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
        require(EmailValidator.getInstance().isValid(email)) { "Invalid email" }
    }
}
