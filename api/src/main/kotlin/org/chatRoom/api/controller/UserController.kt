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
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.User as UserAggregate

class UserController(private val userRepository: UserRepository) {
    private fun fetchUser(call: ApplicationCall) : UserAggregate? {
        val rawId = call.parameters["id"] ?: return null

        val id = try {
            Id(rawId)
        } catch (e: Throwable) {
            return null
        }

        return userRepository.getById(id)
    }

    suspend fun list(call: ApplicationCall) {
        val users = userRepository.getAll()
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

        val user = UserAggregate.create(
            email = payload.email,
            firstName = payload.firstName,
            lastName = payload.lastName,
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
