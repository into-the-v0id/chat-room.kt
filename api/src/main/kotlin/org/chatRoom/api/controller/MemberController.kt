package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Member
import org.chatRoom.api.payload.member.CreateMember
import org.chatRoom.core.repository.MemberRepository
import org.chatRoom.core.repository.RoomRepository
import org.chatRoom.core.repository.UserRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.Member as MemberAggregate

class MemberController(
    private val memberRepository: MemberRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
) {
    private fun fetchMember(call: ApplicationCall) : MemberAggregate? {
        val rawId = call.parameters["memberId"] ?: return null

        val id = try {
            Id(rawId)
        } catch (e: Throwable) {
            return null
        }

        return memberRepository.getById(id)
    }

    suspend fun list(call: ApplicationCall) {
        val rawUserId = call.request.queryParameters["user_id"]
        val userId = if (rawUserId != null) {
            Id.tryFrom(rawUserId) ?: throw BadRequestException("Invalid ID")
        } else {
            null
        }

        val rawRoomId = call.request.queryParameters["room_id"]
        val roomId = if (rawRoomId != null) {
            Id.tryFrom(rawRoomId) ?: throw BadRequestException("Invalid ID")
        } else {
            null
        }

        val members = memberRepository.getAll(
            userIds = if (userId != null) listOf(userId) else null,
            roomIds = if (roomId != null) listOf(roomId) else null,
        )
            .map { memberAggregate -> Member(memberAggregate) }

        call.respond(members)
    }

    suspend fun detail(call: ApplicationCall) {
        val memberAggregate = fetchMember(call) ?: throw NotFoundException()

        val memberModel = Member(memberAggregate)

        call.respond(memberModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateMember>()

        val userAggregate = userRepository.getById(payload.userId) ?: throw BadRequestException("Unknown user")
        val roomAggregate = roomRepository.getById(payload.roomId) ?: throw BadRequestException("Unknown room")

        val member = MemberAggregate.create(
            user = userAggregate,
            room = roomAggregate,
        )
        memberRepository.create(member)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val memberAggregate = fetchMember(call) ?: throw NotFoundException()

        memberRepository.delete(memberAggregate)

        call.respond(HttpStatusCode.OK)
    }
}
