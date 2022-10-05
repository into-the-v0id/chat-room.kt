package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Message
import org.chatRoom.api.payload.message.CreateMessage
import org.chatRoom.core.repository.MemberRepository
import org.chatRoom.core.repository.MessageRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.Message as MessageAggregate

class MessageController(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
) {
    private fun fetchMessage(call: ApplicationCall) : MessageAggregate? {
        val rawId = call.parameters["messageId"] ?: return null
        val id = Id.tryFrom(rawId) ?: return null

        return messageRepository.getById(id)
    }

    suspend fun list(call: ApplicationCall) {
        var memberIds = call.request.queryParameters.getAll("member_id")
            ?.map { rawId -> Id.tryFrom(rawId) ?: throw BadRequestException("Invalid ID") }

        val roomIds = call.request.queryParameters.getAll("room_id")
            ?.map { rawId -> Id.tryFrom(rawId) ?: throw BadRequestException("Invalid ID") }
        if (roomIds != null) {
            val roomMemberAggregates = memberRepository.getAll(roomIds = roomIds)
            val roomMemberIds = roomMemberAggregates.map { member -> member.modelId }

            memberIds = (memberIds ?: roomMemberIds)
                .intersect(roomMemberIds)
                .toList()
        }

        val messages = messageRepository.getAll(memberIds = memberIds)
            .map { messageAggregate -> Message(messageAggregate) }

        call.respond(messages)
    }

    suspend fun detail(call: ApplicationCall) {
        val messageAggregate = fetchMessage(call) ?: throw NotFoundException()
        val messageModel = Message(messageAggregate)

        call.respond(messageModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateMessage>()

        val memberAggregate = memberRepository.getById(payload.memberId) ?: throw BadRequestException("Unknown member")

        val message = MessageAggregate.create(member = memberAggregate, content = payload.content)
        messageRepository.create(message)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val messageAggregate = fetchMessage(call) ?: throw NotFoundException()

        messageRepository.delete(messageAggregate)

        call.respond(HttpStatusCode.OK)
    }
}
