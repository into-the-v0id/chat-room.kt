package org.chatRoom.api.payload.room

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoom(
    val handle: String,
)
