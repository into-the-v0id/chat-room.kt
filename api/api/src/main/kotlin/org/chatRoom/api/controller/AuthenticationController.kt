package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.authentication.SessionPrincipal
import org.chatRoom.api.exception.HttpException
import org.chatRoom.api.resource.Sessions
import org.chatRoom.api.resource.Users
import org.chatRoom.core.model.session.CreatedSession
import org.chatRoom.core.model.user.OwnedUser
import org.chatRoom.core.payload.authentication.Login
import org.chatRoom.core.payload.authentication.Register
import org.chatRoom.core.repository.read.UserQuery
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.SessionWriteRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.valueObject.Password
import org.chatRoom.core.valueObject.Token
import org.chatRoom.core.aggreagte.Session.Companion as SessionAggregate
import org.chatRoom.core.aggreagte.User as UserAggregate

class AuthenticationController(
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
    private val sessionWriteRepository: SessionWriteRepository,
) {
    suspend fun login(call: ApplicationCall) {
        val payload = call.receive<Login>()

        val userAggregate: UserAggregate? = run {
            if (payload.userId != null) return@run userReadRepository.getAll(UserQuery(ids = listOf(payload.userId!!))).firstOrNull()
            if (payload.handle != null) return@run userReadRepository.getAll(UserQuery(handles = listOf(payload.handle!!))).firstOrNull()
            return@run null
        }
        if (userAggregate == null) {
            throw HttpException(HttpStatusCode.Unauthorized, "Invalid credentials")
        }

        val isValidPassword = userAggregate.password.verify(payload.password)
        if (! isValidPassword) {
            throw HttpException(HttpStatusCode.Unauthorized, "Invalid credentials")
        }

        val sessionToken = Token()
        val sessionAggregate = SessionAggregate.create(
            userId = userAggregate.modelId,
            secret = sessionToken,
        )
        sessionWriteRepository.create(sessionAggregate)

        val sessionModel = CreatedSession(sessionAggregate, sessionToken)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Sessions.Detail(sessionAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, sessionModel)
    }

    suspend fun logout(call: ApplicationCall) {
        val session = call.principal<SessionPrincipal>()?.session
        if (session != null) {
            sessionWriteRepository.delete(session)
        }

        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun registration(call: ApplicationCall) {
        val payload = call.receive<Register>()

        val existingUsers = userReadRepository.getAll(UserQuery(handles = listOf(payload.handle)))
        if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

        val userAggregate = UserAggregate.create(
            email = payload.email,
            handle = payload.handle,
            password = Password.create(payload.password)
        )
        userWriteRepository.create(userAggregate)

        val userModel = OwnedUser(userAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Users.Detail(userAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, userModel)
    }
}
