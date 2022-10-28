package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.core.model.Member
import org.chatRoom.core.payload.member.CreateMember
import org.chatRoom.api.resource.Members
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.aggreagte.Member as MemberAggregate

class MemberController(
    private val memberReadRepository: MemberReadRepository,
    private val memberWriteRepository: MemberWriteRepository,
    private val userReadRepository: UserReadRepository,
    private val roomReadRepository: RoomReadRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Members) {
        val ids = resource.ids.ifEmpty { null }
        val userIds = resource.userIds.ifEmpty { null }
        val roomIds = resource.roomIds.ifEmpty { null }

        val memberModels = memberReadRepository.getAll(ids = ids, userIds = userIds, roomIds = roomIds)
            .map { memberAggregate -> Member(memberAggregate) }

        call.respond(memberModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Members.Detail) {
        val memberAggregate = memberReadRepository.getById(resource.id) ?: throw NotFoundException()
        val memberModel = Member(memberAggregate)

        call.respond(memberModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateMember>()

        val userAggregate = userReadRepository.getById(payload.userId) ?: throw BadRequestException("Unknown user")
        val roomAggregate = roomReadRepository.getById(payload.roomId) ?: throw BadRequestException("Unknown room")

        val memberAggregate = MemberAggregate.create(user = userAggregate, room = roomAggregate)
        memberWriteRepository.create(memberAggregate)

        val memberModel = Member(memberAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Members.Detail(memberAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, memberModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Members.Detail) {
        val memberAggregate = memberReadRepository.getById(resource.id) ?: throw NotFoundException()

        memberWriteRepository.delete(memberAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
