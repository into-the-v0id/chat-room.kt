package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.chatRoom.api.controller.MemberController

class MemberRoutes(
    private val memberController: MemberController,
) {
    fun Route.memberRouting() {
        route("members") {
            get { memberController.list(call) }
            post { memberController.create(call) }

            route("{memberId}") {
                get { memberController.detail(call) }
                delete { memberController.delete(call) }
            }
        }
    }
}
