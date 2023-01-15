package org.chatRoom.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val errors: List<Error>) {
    @Serializable
    data class Error(
        val code: String,
        val message: String,
    )
}
