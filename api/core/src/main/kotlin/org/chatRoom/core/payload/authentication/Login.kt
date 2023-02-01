package org.chatRoom.core.payload.authentication

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id

@Serializable
data class Login(
    val userId: Id? = null,
    val handle: Handle? = null,
) {
    init {
        require((userId != null) xor (handle != null)) { "Please specify either a userId or handle" }
    }
}
