package org.chatRoom.api.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator

@Serializable
data class CreateUser(
    val email: String,
    val handle: String,
) {
    init {
        if (! EmailValidator.getInstance().isValid(email)) error("Invalid email")
    }
}
