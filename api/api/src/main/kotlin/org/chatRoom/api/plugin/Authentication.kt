package org.chatRoom.api.plugin

import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.chatRoom.api.authentication.SessionPrincipal
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.valueObject.session.SessionToken
import java.time.Instant

class Authentication(
    private val sessionReadRepository: SessionReadRepository,
) {
    fun Application.configureAuthentication() {
        install(Authentication) {
            bearer {
                authenticate { credential ->
                    val token = try {
                        SessionToken.parse(credential.token)
                    } catch (e: IllegalArgumentException) {
                        // Invalid token
                        return@authenticate null
                    }

                    val sessionAggregate = sessionReadRepository.getAll(SessionQuery(ids = listOf(token.id))).firstOrNull()

                    // Session not found
                    if (sessionAggregate == null) {
                        return@authenticate null
                    }

                    // Mismatching secret
                    if (token.secret != sessionAggregate.secret) {
                        return@authenticate null
                    }

                    // Session expired
                    if (sessionAggregate.dateValidUntil.isBefore(Instant.now())) {
                        return@authenticate null
                    }

                    SessionPrincipal(sessionAggregate)
                }
            }
        }
    }
}
