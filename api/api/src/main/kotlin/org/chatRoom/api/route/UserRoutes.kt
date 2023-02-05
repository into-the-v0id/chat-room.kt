package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.UserController
import org.chatRoom.api.resource.Users

class UserRoutes(
    private val userController: UserController,
) {
    fun Route.userRouting() {
        authenticate("session") {
            get<Users> { resource -> userController.list(call, resource) }

            get<Users.Self> { userController.selfDetail(call) }
            put<Users.Self> { userController.selfUpdate(call) }
            delete<Users.Self> { userController.selfDelete(call) }

            get<Users.Detail> { resource -> userController.detail(call, resource) }
        }
    }
}
