package org.chatRoom.api.plugin

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import org.chatRoom.api.route.userRouting
import org.chatRoom.core.repository.UserRepository

fun Application.configureRouting(userRepository: UserRepository) {
    install(AutoHeadResponse)
    routing {
        route("/users") { userRouting(userRepository) }
    }
}
