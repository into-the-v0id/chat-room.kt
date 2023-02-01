package org.chatRoom.core.payload.authentication

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Id

@Serializable
data class Login(
    val userId: Id? = null,
    val email: EmailAddress? = null,
) {
    init {
        require((userId != null) xor (email != null)) { "Please specify either userId or email" }
    }
}
