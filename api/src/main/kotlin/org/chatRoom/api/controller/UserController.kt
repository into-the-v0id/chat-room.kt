package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.model.User
import org.chatRoom.api.payload.user.CreateUser
import org.chatRoom.api.payload.user.UpdateUser
import org.chatRoom.api.resource.Users
import org.chatRoom.core.repository.UserRepository
import org.chatRoom.core.aggreagte.User as UserAggregate

class UserController(private val userRepository: UserRepository) {
    suspend fun list(call: ApplicationCall, resource: Users) {
        val handles = resource.handles.ifEmpty { null }

        val userModels = userRepository.getAll(handles = handles)
            .map { userAggregate -> User(userAggregate) }

        call.respond(userModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Users.Detail) {
        val userAggregate = userRepository.getById(resource.id) ?: throw NotFoundException()
        val userModel = User(userAggregate)

        call.respond(userModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateUser>()

        val existingUsers = userRepository.getAll(handles = listOf(payload.handle))
        if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

        val userAggregate = UserAggregate.create(email = payload.email, handle = payload.handle)
        userRepository.create(userAggregate)

        val userModel = User(userAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Users.Detail(userAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, userModel)
    }

    suspend fun update(call: ApplicationCall, resource: Users.Detail) {
        var userAggregate = userRepository.getById(resource.id) ?: throw NotFoundException()

        val payload = call.receive<UpdateUser>()

        if (payload.handle != userAggregate.handle) {
            val existingUsers = userRepository.getAll(handles = listOf(payload.handle))
            if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

            userAggregate = userAggregate.changeHandle(payload.handle)
        }
        if (payload.email != userAggregate.email) {
            userAggregate = userAggregate.changeEmail(payload.email)
        }

        userRepository.update(userAggregate)

        val userModel = User(userAggregate)

        call.respond(userModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Users.Detail) {
        val userAggregate = userRepository.getById(resource.id) ?: throw NotFoundException()

        userRepository.delete(userAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
