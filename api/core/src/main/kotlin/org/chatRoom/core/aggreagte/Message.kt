package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.message.ChangeContent
import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.valueObject.Id
import java.time.Instant

class Message(
    modelId: Id,
    memberId: Id,
    content: String,
    dateCreated: Instant = Instant.now(),
    dateUpdated: Instant = dateCreated,
) : Aggregate<MessageEvent>(modelId = modelId) {
    var memberId: Id = memberId
        protected set

    var content: String = content
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    var dateUpdated: Instant = dateUpdated
        protected set

    companion object {
        fun create(member: Member, content: String): Message {
            val event = CreateMessage(
                modelId = Id(),
                memberId = member.modelId,
                content = content,
            )

            return applyEvent(null, event) ?: error("Expected message")
        }

        fun applyEvent(message: Message?, event: MessageEvent): Message? = applyAllEvents(message, listOf(event))

        fun applyAllEvents(message: Message?, events: List<MessageEvent>): Message? = applyAllEvents(message, events) { message, event ->
            var message = message

            when (event) {
                is CreateMessage -> {
                    if (message != null) error("Unexpected message")
                    message = Message(
                        modelId = event.modelId,
                        memberId = event.memberId,
                        content = event.content,
                        dateCreated = event.dateIssued,
                    )
                }
                is ChangeContent -> {
                    if (message == null) error("Expected message")
                    message.content = event.content
                    message.dateUpdated = event.dateIssued
                }
                is DeleteMessage -> {
                    if (message == null) error("Expected message")
                    message = null
                }
            }

            message
        }
    }

    fun changeContent(content: String): Message {
        val event = ChangeContent(
            modelId = this.modelId,
            content = content,
        )

        return applyEvent(this, event) ?: error("Expected message")
    }
}
