package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.model.User
import org.chatRoom.api.payload.user.CreateUser
import org.chatRoom.api.payload.user.UpdateUser
import org.chatRoom.core.repository.UserRepository
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.User as UserAggregate

class UserController(private val userRepository: UserRepository) {
    private fun fetchUser(call: ApplicationCall) : UserAggregate? {
        val rawId = call.parameters["userId"] ?: return null
        val id = Id.tryFrom(rawId) ?: return null

        return userRepository.getById(id)
    }

    suspend fun list(call: ApplicationCall) {
        val handles = call.request.queryParameters.getAll("handle")
            ?.map { rawHandle -> Handle.tryFrom(rawHandle) ?: throw BadRequestException("Invalid handle") }

        val users = userRepository.getAll(handles = handles)
            .map { userAggregate -> User(userAggregate) }

        call.respond(users)
    }

    suspend fun detail(call: ApplicationCall) {
        val userAggregate = fetchUser(call) ?: throw NotFoundException()
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

        call.respond(HttpStatusCode.Created, userModel)
    }

    suspend fun update(call: ApplicationCall) {
        var userAggregate = fetchUser(call) ?: throw NotFoundException()

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

    suspend fun delete(call: ApplicationCall) {
        val userAggregate = fetchUser(call) ?: throw NotFoundException()

        userRepository.delete(userAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
