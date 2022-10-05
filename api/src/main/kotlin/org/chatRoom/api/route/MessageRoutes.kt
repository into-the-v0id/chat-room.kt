package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.chatRoom.api.controller.MessageController

class MessageRoutes(
    private val messageController: MessageController,
) {
    fun Route.messageRouting() {
        route("messages") {
            get { messageController.list(call) }
            post { messageController.create(call) }

            route("{messageId}") {
                get { messageController.detail(call) }
                put { messageController.update(call) }
                delete { messageController.delete(call) }
            }
        }
    }
}
