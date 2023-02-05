package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.AuthenticationController
import org.chatRoom.api.resource.Authentication

class AuthenticationRoutes(
    private val authenticationController: AuthenticationController,
) {
    fun Route.authenticationRouting() {
        authenticate("session", optional = true) {
            post<Authentication.Login> { authenticationController.login(call) }
            post<Authentication.Logout> { authenticationController.logout(call) }
            post<Authentication.Registration> { authenticationController.registration(call) }
        }
    }
}
