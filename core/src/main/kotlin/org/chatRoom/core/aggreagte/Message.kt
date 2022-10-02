package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.message.CreateMessage
import org.chatRoom.core.event.message.DeleteMessage
import org.chatRoom.core.event.message.MessageEvent
import org.chatRoom.core.valueObject.Id
import java.time.Instant

class Message protected constructor(
    modelId: Id,
    memberId: Id,
    content: String,
    dateCreated: Instant = Instant.now(),
) : Aggregate<MessageEvent>(modelId = modelId) {
    var memberId: Id = memberId
        protected set

    var content: String = content
        protected set

    var dateCreated: Instant = dateCreated
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

        fun applyEvent(message: Message?, event: MessageEvent): Message? = applyEvent(message, event, Companion::applyEventInternal)

        fun applyAllEvents(message: Message?, events: List<MessageEvent>): Message? = applyAllEvents(message, events, Companion::applyEventInternal)

        private fun applyEventInternal(message: Message?, event: MessageEvent): Message? {
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
                is DeleteMessage -> {
                    if (message == null) error("Expected message")
                    message = null
                }
                else -> error("Unknown event")
            }

            return message
        }
    }
}
