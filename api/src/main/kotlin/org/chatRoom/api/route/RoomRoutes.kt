package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.chatRoom.api.controller.RoomController

class RoomRoutes(
    private val roomController: RoomController,
) {
    fun Route.roomRouting() {
        route("rooms") {
            get { roomController.list(call) }
            post { roomController.create(call) }

            route("{roomId}") {
                get { roomController.detail(call) }
                put { roomController.update(call) }
                delete { roomController.delete(call) }
            }
        }
    }
}
