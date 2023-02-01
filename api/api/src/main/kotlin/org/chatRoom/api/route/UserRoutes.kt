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
        authenticate {
            get<Users> { resource -> userController.list(call, resource) }
            post<Users> { userController.create(call) }

            get<Users.Detail> { resource -> userController.detail(call, resource) }
            put<Users.Detail> { resource -> userController.update(call, resource) }
            delete<Users.Detail> { resource -> userController.delete(call, resource) }
        }
    }
}
