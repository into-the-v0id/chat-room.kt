package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.authentication.SessionPrincipal
import org.chatRoom.api.exception.HttpException
import org.chatRoom.core.model.Message
import org.chatRoom.core.payload.message.CreateMessage
import org.chatRoom.core.payload.message.UpdateMessage
import org.chatRoom.api.resource.Messages
import org.chatRoom.core.repository.read.*
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.repository.write.update
import org.chatRoom.core.response.ListResponse
import org.chatRoom.core.aggreagte.Message as MessageAggregate

class MessageController(
    private val messageReadRepository: MessageReadRepository,
    private val messageWriteRepository: MessageWriteRepository,
    private val memberReadRepository: MemberReadRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Messages) {
        val session = call.principal<SessionPrincipal>()!!.session
        val memberAggregates = memberReadRepository.getAll(MemberQuery(
            userIds = listOf(session.userId)
        ))
        val allowedMemberAggregates = memberReadRepository.getAll(MemberQuery(
            roomIds = memberAggregates.map { member -> member.roomId }
        ))

        var memberIds = allowedMemberAggregates.map { member -> member.modelId }
        if (resource.memberIds.isNotEmpty()) memberIds = memberIds.intersect(resource.memberIds).toList()
        if (resource.roomIds.isNotEmpty()) {
            val roomMemberAggregates = memberReadRepository.getAll(MemberQuery(roomIds = resource.roomIds))
            val roomMemberIds = roomMemberAggregates.map { member -> member.modelId }
            memberIds = memberIds.intersect(roomMemberIds).toList()
        }

        val query = MessageQuery(
            ids = resource.ids.ifEmpty { null },
            memberIds = memberIds,
            offset = resource.offset,
            limit = resource.limit,
            sortCriteria = resource.sortCriteria,
        )

        val messageModels = messageReadRepository.getAll(query)
            .map { messageAggregate -> Message(messageAggregate) }

        val listResponse = ListResponse(
            data = messageModels,
            list = ListResponse.ListInfo(
                offset = resource.offset,
                limit = resource.limit,
                currentItemCount = messageModels.size,
                totalItemCount = messageReadRepository.count(query.copy(offset = null, limit = null)),
            )
        )

        call.respond(listResponse)
    }

    suspend fun detail(call: ApplicationCall, resource: Messages.Detail) {
        val messageAggregate = messageReadRepository.getById(resource.id) ?: throw NotFoundException()
        val messageMemberAggregate = memberReadRepository.getById(messageAggregate.memberId)!!

        val session = call.principal<SessionPrincipal>()!!.session
        val memberAggregate = memberReadRepository.getAll(MemberQuery(
            userIds = listOf(session.userId),
            roomIds = listOf(messageMemberAggregate.roomId)
        )).firstOrNull()
        if (memberAggregate == null) throw NotFoundException()

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
        val memberAggregate = memberReadRepository.getById(messageAggregate.memberId)!!

        val session = call.principal<SessionPrincipal>()!!.session
        if (session.userId != memberAggregate.userId) throw HttpException(HttpStatusCode.Forbidden)

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
        val memberAggregate = memberReadRepository.getById(messageAggregate.memberId)!!

        val session = call.principal<SessionPrincipal>()!!.session
        if (session.userId != memberAggregate.userId) throw HttpException(HttpStatusCode.Forbidden)

        messageWriteRepository.delete(messageAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
