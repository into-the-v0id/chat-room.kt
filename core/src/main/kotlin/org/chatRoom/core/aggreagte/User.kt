package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.user.ChangeEmail
import org.chatRoom.core.event.user.CreateUser
import org.chatRoom.core.event.user.DeleteUser
import org.chatRoom.core.event.user.UserEvent
import org.chatRoom.core.valueObject.Id
import java.time.Instant
import java.time.OffsetDateTime

class User protected constructor(
    modelId: Id,
    email: String,
    handle: String,
    dateCreated: Instant = Instant.now(),
) : Aggregate<UserEvent>(modelId = modelId) {
    var email: String = email
        protected set

    var handle: String = handle
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    companion object {
        fun create(email: String, handle: String): User {
            val event = CreateUser(
                modelId = Id(),
                email = email,
                handle = handle,
            )

            return applyEvent(null, event) ?: error("Expected user")
        }

        fun applyEvent(user: User?, event: UserEvent): User? = applyEvent(user, event, Companion::applyEventInternal)

        fun applyAllEvents(user: User?, events: List<UserEvent>): User? = applyAllEvents(user, events, Companion::applyEventInternal)

        private fun applyEventInternal(user: User?, event: UserEvent): User? {
            var user = user

            when (event) {
                is CreateUser -> {
                    if (user != null) error("Unexpected user")
                    user = User(
                        modelId = event.modelId,
                        email = event.email,
                        handle = event.handle,
                        dateCreated = event.dateIssued,
                    )
                }
                is ChangeEmail -> {
                    if (user == null) error("Expected user")
                    user.email = event.email
                }
                is DeleteUser -> {
                    if (user == null) error("Expected user")
                    user = null
                }
                else -> error("Unknown event")
            }

            return user
        }
    }

    fun changeEmail(email: String): User {
        val event = ChangeEmail(
            modelId = this.modelId,
            email = email,
        )

        return applyEvent(this, event) ?: error("Expected user")
    }
}
