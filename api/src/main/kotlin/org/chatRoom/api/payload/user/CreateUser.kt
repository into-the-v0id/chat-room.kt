package org.chatRoom.api.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator
import org.chatRoom.api.validator.HandleValidator

@Serializable
data class CreateUser(
    val email: String,
    val handle: String,
) {
    init {
        if (! EmailValidator.getInstance().isValid(email)) error("Invalid email")
        if (! HandleValidator.instance.isValid(handle)) error("Invalid handle")
    }
}
