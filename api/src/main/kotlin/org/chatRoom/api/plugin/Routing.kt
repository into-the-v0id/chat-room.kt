package org.chatRoom.api.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.routing.*
import org.chatRoom.api.route.UserRoutes

class Routing(
    private val userRoutes: UserRoutes,
) {
    fun Application.configureRouting() {
        install(AutoHeadResponse)

        routing {
            userRoutes.apply { userRouting() }
        }
    }
}
