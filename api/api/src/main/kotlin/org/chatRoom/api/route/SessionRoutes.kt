package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.SessionController
import org.chatRoom.api.resource.Sessions

class SessionRoutes(
    private val sessionController: SessionController,
) {
    fun Route.sessionRouting() {
        authenticate("session") {
            get<Sessions> { resource -> sessionController.list(call, resource) }

            get<Sessions.Detail> { resource -> sessionController.detail(call, resource) }
            delete<Sessions.Detail> { resource -> sessionController.delete(call, resource) }
        }
    }
}
