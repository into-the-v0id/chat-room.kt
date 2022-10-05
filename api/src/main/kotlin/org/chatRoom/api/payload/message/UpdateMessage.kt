package org.chatRoom.api.payload.message

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMessage(val content: String)
