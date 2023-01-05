package org.chatRoom.core.payload.room

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Handle

@Serializable
data class CreateRoom(val handle: Handle)
