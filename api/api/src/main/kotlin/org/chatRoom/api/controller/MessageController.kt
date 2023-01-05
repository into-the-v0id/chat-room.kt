package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.core.model.Message
import org.chatRoom.core.payload.message.CreateMessage
import org.chatRoom.core.payload.message.UpdateMessage
import org.chatRoom.api.resource.Messages
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.repository.write.update
import org.chatRoom.core.aggreagte.Message as MessageAggregate

class MessageController(
    private val messageReadRepository: MessageReadRepository,
    private val messageWriteRepository: MessageWriteRepository,
    private val memberReadRepository: MemberReadRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Messages) {
        val ids = resource.ids.ifEmpty { null }

        var memberIds = resource.memberIds.ifEmpty { null }

        val roomIds = resource.roomIds
        if (roomIds.isNotEmpty()) {
            val roomMemberAggregates = memberReadRepository.getAll(roomIds = roomIds)
            val roomMemberIds = roomMemberAggregates.map { member -> member.modelId }

            memberIds = (memberIds ?: roomMemberIds)
                .intersect(roomMemberIds)
                .toList()
        }

        val messageModels = messageReadRepository.getAll(
            ids = ids,
            memberIds = memberIds,
            offset = resource.offset,
            limit = resource.limit,
            sortCriteria = resource.sortCriteria,
        ).map { messageAggregate -> Message(messageAggregate) }

        call.respond(messageModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Messages.Detail) {
        val messageAggregate = messageReadRepository.getById(resource.id) ?: throw NotFoundException()
        val messageModel = Message(messageAggregate)

        call.respond(messageModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateMessage>()

        val memberAggregate = memberReadRepository.getById(payload.memberId) ?: throw BadRequestException("Unknown member")

        val messageAggregate = MessageAggregate.create(member = memberAggregate, content = payload.content)
        messageWriteRepository.create(messageAggregate)

        val messageModel = Message(messageAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Messages.Detail(messageAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, messageModel)
    }

    suspend fun update(call: ApplicationCall, resource: Messages.Detail) {
        var messageAggregate = messageReadRepository.getById(resource.id) ?: throw NotFoundException()

        val payload = call.receive<UpdateMessage>()

        if (payload.id != null && payload.id != messageAggregate.modelId) throw BadRequestException("Mismatching IDs")
        if (payload.memberId != null && payload.memberId != messageAggregate.memberId) throw BadRequestException("Attempting to modify read-only property 'memberId'")
        if (payload.content != messageAggregate.content) messageAggregate = messageAggregate.changeContent(payload.content)

        messageWriteRepository.update(messageAggregate)

        val messageModel = Message(messageAggregate)

        call.respond(messageModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Messages.Detail) {
        val messageAggregate = messageReadRepository.getById(resource.id) ?: throw NotFoundException()

        messageWriteRepository.delete(messageAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
