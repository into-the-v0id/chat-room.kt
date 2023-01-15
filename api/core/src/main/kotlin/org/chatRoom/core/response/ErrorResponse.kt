package org.chatRoom.core.response

import kotlinx.serialization.Serializable
import kotlin.collections.List

@Serializable
data class ErrorResponse(val errors: List<Error>) {
    @Serializable
    data class Error(
        val code: String,
        val message: String,
    )
}
