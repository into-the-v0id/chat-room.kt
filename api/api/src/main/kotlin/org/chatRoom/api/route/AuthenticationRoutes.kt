package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.AuthenticationController
import org.chatRoom.api.resource.Authentication

class AuthenticationRoutes(
    private val authenticationController: AuthenticationController,
) {
    fun Route.authenticationRouting() {
        post<Authentication.Login> { authenticationController.login(call) }
        post<Authentication.Registration> { authenticationController.registration(call) }
    }
}
