package org.chatRoom.api.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator

@Serializable
data class UpdateUser(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
) {
    init {
        if (! EmailValidator.getInstance().isValid(email)) error("Invalid email")
    }
}
