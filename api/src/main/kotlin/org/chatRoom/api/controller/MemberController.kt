package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Member
import org.chatRoom.api.payload.member.CreateMember
import org.chatRoom.core.repository.MemberRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.Member as MemberAggregate

class MemberController(private val memberRepository: MemberRepository) {
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
        val members = memberRepository.getAll()
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

        val member = MemberAggregate.create(
            userId = payload.userId,
            roomId = payload.roomId,
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
