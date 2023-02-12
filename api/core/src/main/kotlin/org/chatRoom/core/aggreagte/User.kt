package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.user.*
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.PasswordHash
import java.time.Instant

class User(
    modelId: Id,
    handle: Handle,
    email: EmailAddress,
    passwordHash: PasswordHash,
    dateCreated: Instant = Instant.now(),
    dateUpdated: Instant = dateCreated,
) : Aggregate<UserEvent>(modelId = modelId) {
    var handle: Handle = handle
        protected set

    var email: EmailAddress = email
        protected set

    var passwordHash: PasswordHash = passwordHash
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    var dateUpdated: Instant = dateUpdated
        protected set

    companion object {
        fun create(email: EmailAddress, handle: Handle, password: String): User {
            val event = CreateUser(
                modelId = Id(),
                email = email,
                handle = handle,
                passwordHash = PasswordHash.create(password),
            )

            return applyEvent(null, event) ?: error("Expected user")
        }

        fun applyEvent(user: User?, event: UserEvent): User? = applyAllEvents(user, listOf(event))

        fun applyAllEvents(user: User?, events: List<UserEvent>): User? = applyAllEvents(user, events) { user, event ->
            var user = user

            when (event) {
                is CreateUser -> {
                    if (user != null) error("Unexpected user")
                    user = User(
                        modelId = event.modelId,
                        email = event.email,
                        handle = event.handle,
                        passwordHash = event.passwordHash,
                        dateCreated = event.dateIssued,
                    )
                }
                is ChangeHandle -> {
                    if (user == null) error("Expected user")
                    user.handle = event.handle
                    user.dateUpdated = event.dateIssued
                }
                is ChangeEmail -> {
                    if (user == null) error("Expected user")
                    user.email = event.email
                    user.dateUpdated = event.dateIssued
                }
                is DeleteUser -> {
                    if (user == null) error("Expected user")
                    user = null
                }
            }

            user
        }
    }

    fun changeHandle(handle: Handle): User {
        val event = ChangeHandle(
            modelId = this.modelId,
            handle = handle,
        )

        return applyEvent(this, event) ?: error("Expected user")
    }

    fun changeEmail(email: EmailAddress): User {
        val event = ChangeEmail(
            modelId = this.modelId,
            email = email,
        )

        return applyEvent(this, event) ?: error("Expected user")
    }
}
