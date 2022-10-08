package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.RoomController
import org.chatRoom.api.resource.Rooms

class RoomRoutes(
    private val roomController: RoomController,
) {
    fun Route.roomRouting() {
        get<Rooms> { roomController.list(call) }
        post<Rooms> { roomController.create(call) }

        get<Rooms.Detail> { resource -> roomController.detail(call, resource) }
        put<Rooms.Detail> { resource -> roomController.update(call, resource) }
        delete<Rooms.Detail> { resource -> roomController.delete(call, resource) }
    }
}
