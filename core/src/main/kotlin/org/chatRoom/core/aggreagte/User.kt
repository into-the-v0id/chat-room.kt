package org.chatRoom.core.aggreagte

import org.chatRoom.core.events.user.ChangeEmail
import org.chatRoom.core.events.user.CreateUser
import org.chatRoom.core.events.user.DeleteUser
import org.chatRoom.core.events.user.UserEvent
import org.chatRoom.core.valueObject.Id
import java.util.Date

class User private constructor(
    modelId: Id,
    email: String,
    firstName: String? = null,
    lastName: String? = null,
    dateCreated: Date = Date(),
) : Aggregate<UserEvent>(modelId = modelId) {
    var email: String = email
        protected set

    var firstName: String? = firstName
        protected set

    var lastName: String? = lastName
        protected set

    var dateCreated: Date = dateCreated
        protected set

    companion object {
        fun create(
            email: String,
            firstName: String? = null,
            lastName: String? = null,
        ): User {
            val event = CreateUser(
                modelId = Id(),
                email = email,
                firstName = firstName,
                lastName = lastName,
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
                        firstName = event.firstName,
                        lastName = event.lastName,
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
