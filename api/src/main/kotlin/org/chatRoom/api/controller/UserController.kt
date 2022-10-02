package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.chatRoom.api.model.User
import org.chatRoom.core.repository.UserRepository
import org.chatRoom.core.valueObject.Id

class UserController(private val userRepository: UserRepository) {
    suspend fun list(call: ApplicationCall) {
        val users = userRepository.getAll()
            .map { userAggregate -> User(userAggregate) }

        call.respond(users)
    }

    suspend fun detail(call: ApplicationCall) {
        val rawId = call.parameters["id"]!!

        val id = try {
            Id(rawId)
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val userAggregate = userRepository.getById(id)
        if (userAggregate == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val userModel = User(userAggregate)
        call.respond(userModel)
    }

    suspend fun create(call: ApplicationCall) {
        TODO("create user")
    }

    suspend fun update(call: ApplicationCall) {
        TODO("update user")
    }

    suspend fun delete(call: ApplicationCall) {
        val rawId = call.parameters["id"]!!

        val id = try {
            Id(rawId)
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val userAggregate = userRepository.getById(id)
        if (userAggregate == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        userRepository.delete(userAggregate)

        call.respond(HttpStatusCode.OK)
    }
}
