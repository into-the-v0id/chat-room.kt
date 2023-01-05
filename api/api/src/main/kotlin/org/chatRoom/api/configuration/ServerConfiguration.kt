package org.chatRoom.api.configuration

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfiguration(
    val host: String,
    val port: UShort,
)
