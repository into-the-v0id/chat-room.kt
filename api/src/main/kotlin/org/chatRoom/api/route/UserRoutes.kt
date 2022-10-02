package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.chatRoom.api.controller.UserController

class UserRoutes(
    private val userController: UserController,
) {
    fun Route.userRouting() {
        route("users") {
            get { userController.list(call) }
            post { userController.create(call) }

            route("{userId}") {
                get { userController.detail(call) }
                put { userController.update(call) }
                delete { userController.delete(call) }
            }
        }
    }
}
