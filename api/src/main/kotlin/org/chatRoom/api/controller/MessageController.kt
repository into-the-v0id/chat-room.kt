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
        val rawMemberId = call.request.queryParameters["member_id"]
        val memberId = if (rawMemberId != null) {
            Id.tryFrom(rawMemberId) ?: throw BadRequestException("Invalid ID")
        } else {
            null
        }

        val messages = messageRepository.getAll(memberId = memberId)
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

        val message = MessageAggregate.create(
            memberId = memberAggregate.modelId,
            content = payload.content,
        )
        messageRepository.create(message)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val messageAggregate = fetchMessage(call) ?: throw NotFoundException()

        messageRepository.delete(messageAggregate)

        call.respond(HttpStatusCode.OK)
    }
}
