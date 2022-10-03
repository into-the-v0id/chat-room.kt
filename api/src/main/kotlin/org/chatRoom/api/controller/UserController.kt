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
        val rawHandle = call.request.queryParameters["handle"]
        val handle = if (rawHandle != null) {
            Handle.tryFrom(rawHandle) ?: throw BadRequestException("Invalid handle")
        } else {
            null
        }

        val users = userRepository.getAll(handle = handle)
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

        val existingUsers = userRepository.getAll(handle = payload.handle)
        if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

        val user = UserAggregate.create(
            email = payload.email,
            handle = payload.handle,
        )
        userRepository.create(user)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun update(call: ApplicationCall) {
        var userAggregate = fetchUser(call) ?: throw NotFoundException()

        val payload = call.receive<UpdateUser>()

        if (payload.email != userAggregate.email) {
            userAggregate = userAggregate.changeEmail(payload.email)
        }

        // TODO: allow update of firstName & lastName

        userRepository.update(userAggregate)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val userAggregate = fetchUser(call) ?: throw NotFoundException()

        userRepository.delete(userAggregate)

        call.respond(HttpStatusCode.OK)
    }
}
