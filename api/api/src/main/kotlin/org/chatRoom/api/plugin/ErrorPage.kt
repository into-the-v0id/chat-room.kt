package org.chatRoom.api.plugin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.logging.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException
import org.chatRoom.api.exception.HttpException
import org.chatRoom.core.response.ErrorResponse
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeoutException

fun Application.configureErrorPage() {
    install(StatusPages) {
        fun errorFactory(status: HttpStatusCode) = ErrorResponse.Error(
            code = "http_${ status.value }",
            message = status.description,
        )
        fun statusErrorPairFactory(status: HttpStatusCode) = Pair(status, errorFactory(status))

        exception<Throwable> { call, exception ->
            call.application.logException(call, exception)

            val (status, error) = when (exception) {
                is HttpException -> Pair(exception.status, ErrorResponse.Error(
                    code = "http_${ exception.status.value }",
                    message = exception.message ?: exception.status.description,
                ))
                is BadRequestException -> {
                    var message = exception.message
                    var code = "http_${ HttpStatusCode.BadRequest.value }"

                    val exceptionCause = exception.cause
                    if (exceptionCause is SerializationException || exceptionCause is IllegalArgumentException) {
                        if (message != null) message += ": "
                        message += "${ exceptionCause.message }"
                        code = exceptionCause::class.qualifiedName ?: code
                    }

                    val error = ErrorResponse.Error(
                        code = code,
                        message = message ?: HttpStatusCode.BadRequest.description,
                    )

                    Pair(HttpStatusCode.BadRequest, error)
                }
                is NotFoundException -> statusErrorPairFactory(HttpStatusCode.NotFound)
                is UnsupportedMediaTypeException -> statusErrorPairFactory(HttpStatusCode.UnsupportedMediaType)
                is TimeoutException, is TimeoutCancellationException -> statusErrorPairFactory(HttpStatusCode.GatewayTimeout)
                else -> statusErrorPairFactory(HttpStatusCode.InternalServerError)
            }

            val errorResponse = ErrorResponse(listOf(error))

            call.respond(status, errorResponse)
        }

        status(
            HttpStatusCode.BadRequest,
            HttpStatusCode.Unauthorized,
            HttpStatusCode.Forbidden,
            HttpStatusCode.NotFound,
        ) { call, status ->
            val errorResponse = ErrorResponse(listOf(
                errorFactory(status),
            ))

            call.respond(status, errorResponse)
        }
    }
}

fun Application.logException(call: ApplicationCall, exception: Throwable) {
    try {
        val status = call.response.status() ?: "Unhandled"
        val logString = try {
            call.request.toLogString()
        } catch (cause: Throwable) {
            "(request error: $cause)"
        }
        val infoString = "$status: $logString. Exception ${exception::class.qualifiedName}: ${exception.message}"

        when (exception) {
            is HttpException,
            is BadRequestException,
            is NotFoundException,
            is UnsupportedMediaTypeException -> log.debug(infoString, exception)
            else -> log.error(infoString, exception)
        }
    } catch (_: OutOfMemoryError) {
        log.error("Exception ${exception::class.qualifiedName}: ${exception.message}")
    }
}
