package org.chatRoom.api.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.chatRoom.api.route.*

class Routing(
    private val authenticationRoutes: AuthenticationRoutes,
    private val userRoutes: UserRoutes,
    private val roomRoutes: RoomRoutes,
    private val memberRoutes: MemberRoutes,
    private val messageRoutes: MessageRoutes,
    private val sessionRoutes: SessionRoutes,
) {
    fun Application.configureRouting() {
        install(AutoHeadResponse)
        install(Resources)

        routing {
            authenticationRoutes.apply { authenticationRouting() }
            userRoutes.apply { userRouting() }
            roomRoutes.apply { roomRouting() }
            memberRoutes.apply { memberRouting() }
            messageRoutes.apply { messageRouting() }
            sessionRoutes.apply { sessionRouting() }
        }
    }
}
