package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Member
import org.chatRoom.api.payload.member.CreateMember
import org.chatRoom.api.resource.Members
import org.chatRoom.core.repository.MemberRepository
import org.chatRoom.core.repository.RoomRepository
import org.chatRoom.core.repository.UserRepository
import org.chatRoom.core.aggreagte.Member as MemberAggregate

class MemberController(
    private val memberRepository: MemberRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Members) {
        val ids = resource.ids.ifEmpty { null }
        val userIds = resource.userIds.ifEmpty { null }
        val roomIds = resource.roomIds.ifEmpty { null }

        val memberModels = memberRepository.getAll(ids = ids, userIds = userIds, roomIds = roomIds)
            .map { memberAggregate -> Member(memberAggregate) }

        call.respond(memberModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Members.Detail) {
        val memberAggregate = memberRepository.getById(resource.id) ?: throw NotFoundException()
        val memberModel = Member(memberAggregate)

        call.respond(memberModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateMember>()

        val userAggregate = userRepository.getById(payload.userId) ?: throw BadRequestException("Unknown user")
        val roomAggregate = roomRepository.getById(payload.roomId) ?: throw BadRequestException("Unknown room")

        val memberAggregate = MemberAggregate.create(user = userAggregate, room = roomAggregate)
        memberRepository.create(memberAggregate)

        val memberModel = Member(memberAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Members.Detail(memberAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, memberModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Members.Detail) {
        val memberAggregate = memberRepository.getById(resource.id) ?: throw NotFoundException()

        memberRepository.delete(memberAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
