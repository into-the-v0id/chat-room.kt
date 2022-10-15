package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Message
import org.chatRoom.api.payload.message.CreateMessage
import org.chatRoom.api.payload.message.UpdateMessage
import org.chatRoom.api.resource.Messages
import org.chatRoom.core.repository.MemberRepository
import org.chatRoom.core.repository.MessageRepository
import org.chatRoom.core.aggreagte.Message as MessageAggregate

class MessageController(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Messages) {
        var memberIds = resource.memberIds.ifEmpty { null }

        val roomIds = resource.roomIds
        if (roomIds.isNotEmpty()) {
            val roomMemberAggregates = memberRepository.getAll(roomIds = roomIds)
            val roomMemberIds = roomMemberAggregates.map { member -> member.modelId }

            memberIds = (memberIds ?: roomMemberIds)
                .intersect(roomMemberIds)
                .toList()
        }

        val messageModels = messageRepository.getAll(memberIds = memberIds)
            .map { messageAggregate -> Message(messageAggregate) }

        call.respond(messageModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Messages.Detail) {
        val messageAggregate = messageRepository.getById(resource.id) ?: throw NotFoundException()
        val messageModel = Message(messageAggregate)

        call.respond(messageModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateMessage>()

        val memberAggregate = memberRepository.getById(payload.memberId) ?: throw BadRequestException("Unknown member")

        val messageAggregate = MessageAggregate.create(member = memberAggregate, content = payload.content)
        messageRepository.create(messageAggregate)

        val messageModel = Message(messageAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Messages.Detail(messageAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, messageModel)
    }

    suspend fun update(call: ApplicationCall, resource: Messages.Detail) {
        var messageAggregate = messageRepository.getById(resource.id) ?: throw NotFoundException()

        val payload = call.receive<UpdateMessage>()

        if (payload.id != messageAggregate.modelId) throw BadRequestException("Mismatching IDs")
        if (payload.memberId != messageAggregate.memberId) throw BadRequestException("Attempting to modify read-only property 'memberId'")
        if (payload.content != messageAggregate.content) messageAggregate = messageAggregate.changeContent(payload.content)

        messageRepository.update(messageAggregate)

        val messageModel = Message(messageAggregate)

        call.respond(messageModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Messages.Detail) {
        val messageAggregate = messageRepository.getById(resource.id) ?: throw NotFoundException()

        messageRepository.delete(messageAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
