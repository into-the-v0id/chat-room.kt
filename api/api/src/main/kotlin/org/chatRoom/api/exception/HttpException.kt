package org.chatRoom.api.exception

import io.ktor.http.*

class HttpException(
    val status: HttpStatusCode,
    message: String?,
): Throwable(message)
