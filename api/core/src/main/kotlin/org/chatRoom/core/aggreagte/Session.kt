package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.session.CreateSession
import org.chatRoom.core.event.session.DeleteSession
import org.chatRoom.core.event.session.*
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Token
import java.time.Duration
import java.time.Instant

class Session(
    modelId: Id,
    userId: Id,
    token: Token,
    dateValidUntil: Instant,
    dateCreated: Instant = Instant.now(),
) : Aggregate<SessionEvent>(modelId = modelId) {
    var userId: Id = userId
        protected set

    var token: Token = token
        protected set

    var dateValidUntil: Instant = dateValidUntil
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    companion object {
        fun create(userId: Id): Session {
            val event = CreateSession(
                modelId = Id(),
                userId = userId,
                token = Token(),
                dateValidUntil = Instant.now().plus(Duration.ofHours(6))
            )

            return applyEvent(null, event) ?: error("Expected session")
        }

        fun applyEvent(session: Session?, event: SessionEvent): Session? = applyAllEvents(session, listOf(event))

        fun applyAllEvents(session: Session?, events: List<SessionEvent>): Session? = applyAllEvents(session, events) { session, event ->
            var session = session

            when (event) {
                is CreateSession -> {
                    if (session != null) error("Unexpected session")
                    session = Session(
                        modelId = event.modelId,
                        userId = event.userId,
                        token = event.token,
                        dateValidUntil = event.dateValidUntil,
                        dateCreated = event.dateIssued,
                    )
                }
                is DeleteSession -> {
                    if (session == null) error("Expected session")
                    session = null
                }
            }

            session
        }
    }
}
