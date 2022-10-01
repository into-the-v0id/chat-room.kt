package org.chatRoom.api.plugin

import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.chatRoom.api.route.userRouting
import org.chatRoom.core.repository.UserRepository

fun Application.configureRouting(userRepository: UserRepository) {
    routing {
        route("/users") { userRouting(userRepository) }
    }
}
