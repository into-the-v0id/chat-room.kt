package org.chatRoom.api.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.chatRoom.api.route.MemberRoutes
import org.chatRoom.api.route.MessageRoutes
import org.chatRoom.api.route.RoomRoutes
import org.chatRoom.api.route.UserRoutes

class Routing(
    private val userRoutes: UserRoutes,
    private val roomRoutes: RoomRoutes,
    private val memberRoutes: MemberRoutes,
    private val messageRoutes: MessageRoutes,
) {
    fun Application.configureRouting() {
        install(AutoHeadResponse)
        install(Resources)

        routing {
            userRoutes.apply { userRouting() }
            roomRoutes.apply { roomRouting() }
            memberRoutes.apply { memberRouting() }
            messageRoutes.apply { messageRouting() }
        }
    }
}
