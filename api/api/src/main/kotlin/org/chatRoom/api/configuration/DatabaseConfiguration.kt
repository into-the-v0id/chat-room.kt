package org.chatRoom.api.configuration

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfiguration(
    val host: String,
    val port: UShort,
    val name: String,
    val user: String?,
    val password: String?,
    val schema: String? = null,
)
