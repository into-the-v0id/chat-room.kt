package org.chatRoom.api.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.chatRoom.api.model.User
import org.chatRoom.core.repository.UserRepository
import org.chatRoom.core.valueObject.Id

fun Route.userRouting(userRepository: UserRepository) {
    get {
        val users = userRepository.getAll()
            .map { userAggregate -> User(userAggregate) }

        call.respond(users)
    }

    post {
        TODO("create user")
    }

    route("{id}") {
        get {
            val rawId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val id = try {
                Id(rawId)
            } catch (e: Throwable) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val userAggregate = userRepository.getById(id)
            if (userAggregate == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val userModel = User(userAggregate)
            call.respond(userModel)
        }

        put {
            TODO("update user")
        }

        delete {
            val rawId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val id = try {
                Id(rawId)
            } catch (e: Throwable) {
                return@delete call.respond(HttpStatusCode.BadRequest)
            }

            val userAggregate = userRepository.getById(id)
            if (userAggregate == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            userRepository.delete(userAggregate)

            call.respond(HttpStatusCode.OK)
        }
    }
}
