package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.user.*
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import java.time.Instant

class User protected constructor(
    modelId: Id,
    email: String,
    handle: Handle,
    dateCreated: Instant = Instant.now(),
) : Aggregate<UserEvent>(modelId = modelId) {
    var email: String = email
        protected set

    var handle: Handle = handle
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    companion object {
        fun create(email: String, handle: Handle): User {
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
                is ChangeHandle -> {
                    if (user == null) error("Expected user")
                    user.handle = event.handle
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

    fun changeHandle(handle: Handle): User {
        val event = ChangeHandle(
            modelId = this.modelId,
            handle = handle,
        )

        return applyEvent(this, event) ?: error("Expected user")
    }

    fun changeEmail(email: String): User {
        val event = ChangeEmail(
            modelId = this.modelId,
            email = email,
        )

        return applyEvent(this, event) ?: error("Expected user")
    }
}
