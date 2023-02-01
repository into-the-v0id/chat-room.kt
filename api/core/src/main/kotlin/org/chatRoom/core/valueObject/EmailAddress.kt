package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator

@Serializable
@JvmInline
value class EmailAddress(private val email: String) {
    init {
        require(EmailValidator.getInstance().isValid(email)) { "Invalid email address" }
    }

    override fun toString(): String = email
}
