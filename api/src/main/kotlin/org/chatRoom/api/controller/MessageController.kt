package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Message
import org.chatRoom.api.payload.message.CreateMessage
import org.chatRoom.core.repository.MessageRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.Message as MessageAggregate

class MessageController(private val messageRepository: MessageRepository) {
    private fun fetchMessage(call: ApplicationCall) : MessageAggregate? {
        val rawId = call.parameters["messageId"] ?: return null

        val id = try {
            Id(rawId)
        } catch (e: Throwable) {
            return null
        }

        return messageRepository.getById(id)
    }

    suspend fun list(call: ApplicationCall) {
        val messages = messageRepository.getAll()
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

        val message = MessageAggregate.create(
            memberId = payload.memberId,
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
