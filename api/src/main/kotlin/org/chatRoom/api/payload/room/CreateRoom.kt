package org.chatRoom.api.payload.room

import kotlinx.serialization.Serializable
import org.chatRoom.api.validator.HandleValidator

@Serializable
data class CreateRoom(
    val handle: String,
) {
    init {
        if (! HandleValidator.instance.isValid(handle)) error("Invalid handle")
    }
}
