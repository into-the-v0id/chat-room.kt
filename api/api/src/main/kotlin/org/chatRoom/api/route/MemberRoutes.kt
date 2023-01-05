package org.chatRoom.api.route

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import org.chatRoom.api.controller.MemberController
import org.chatRoom.api.resource.Members

class MemberRoutes(
    private val memberController: MemberController,
) {
    fun Route.memberRouting() {
        get<Members> { resource -> memberController.list(call, resource) }
        post<Members> { memberController.create(call) }

        get<Members.Detail> { resource -> memberController.detail(call, resource) }
        delete<Members.Detail> { resource -> memberController.delete(call, resource) }
    }
}
