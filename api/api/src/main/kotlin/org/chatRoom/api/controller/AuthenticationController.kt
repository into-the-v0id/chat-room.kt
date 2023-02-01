package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.exception.HttpException
import org.chatRoom.api.resource.Sessions
import org.chatRoom.core.model.Session
import org.chatRoom.core.payload.authentication.Login
import org.chatRoom.core.repository.read.UserQuery
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.SessionWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.aggreagte.Session.Companion as SessionAggregate

class AuthenticationController(
    private val userReadRepository: UserReadRepository,
    private val sessionWriteRepository: SessionWriteRepository,
) {
    suspend fun login(call: ApplicationCall) {
        val payload = call.receive<Login>()

        val userAggregate = userReadRepository.getAll(UserQuery(emails = listOf(payload.email))).firstOrNull()
        if (userAggregate === null) {
            throw HttpException(HttpStatusCode.Unauthorized, "Invalid credentials")
        }

        val sessionAggregate = SessionAggregate.create(userId = userAggregate.modelId)
        sessionWriteRepository.create(sessionAggregate)

        val sessionModel = Session(sessionAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Sessions.Detail(sessionAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, sessionModel)
    }
}
