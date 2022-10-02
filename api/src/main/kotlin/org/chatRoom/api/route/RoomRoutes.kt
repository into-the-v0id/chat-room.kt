package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.chatRoom.api.controller.MemberController
import org.chatRoom.api.controller.MessageController
import org.chatRoom.api.controller.RoomController

class RoomRoutes(
    private val roomController: RoomController,
    private val memberController: MemberController,
    private val messageController: MessageController,
) {
    fun Route.roomRouting() {
        route("/rooms") {
            get { roomController.list(call) }
            post { roomController.create(call) }

            route("{id}") {
                get { roomController.detail(call) }
                delete { roomController.delete(call) }

                route("members") {
                    get { memberController.list(call) }
                    post { memberController.create(call) }

                    route("{id}") {
                        get { memberController.detail(call) }
                        delete { memberController.delete(call) }
                    }
                }

                route("messages") {
                    get { messageController.list(call) }
                    post { messageController.create(call) }

                    route("{id}") {
                        get { messageController.detail(call) }
                        delete { messageController.delete(call) }
                    }
                }
            }
        }
    }
}
