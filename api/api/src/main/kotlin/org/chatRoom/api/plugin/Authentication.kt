package org.chatRoom.api.plugin

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.valueObject.Token
import java.time.Instant

class Authentication(
    private val sessionReadRepository: SessionReadRepository,
) {
    fun Application.configureAuthentication() {
        install(Authentication) {
            bearer {
                authenticate { credential ->
                    val token = try {
                        Token(credential.token)
                    } catch (e: IllegalArgumentException) {
                        // Invalid token
                        throw BadRequestException("Invalid authentication token")
                    }

                    val sessionAggregate = sessionReadRepository.getAll(SessionQuery(tokens = listOf(token))).firstOrNull()

                    // Token not found
                    if (sessionAggregate == null) {
                        return@authenticate null
                    }

                    // Session expired
                    if (sessionAggregate.dateValidUntil.isBefore(Instant.now())) {
                        return@authenticate null
                    }

                    UserIdPrincipal(sessionAggregate.userId.toString())
                }
            }
        }
    }
}
