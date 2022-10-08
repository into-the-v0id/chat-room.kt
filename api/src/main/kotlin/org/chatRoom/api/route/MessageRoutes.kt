package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.MessageController
import org.chatRoom.api.resource.Messages

class MessageRoutes(
    private val messageController: MessageController,
) {
    fun Route.messageRouting() {
        get<Messages> { resource -> messageController.list(call, resource) }
        post<Messages> { messageController.create(call) }

        get<Messages.Detail> { resource -> messageController.detail(call, resource) }
        put<Messages.Detail> { resource -> messageController.update(call, resource) }
        delete<Messages.Detail> { resource -> messageController.delete(call, resource) }
    }
}
