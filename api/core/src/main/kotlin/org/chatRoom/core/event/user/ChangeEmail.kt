package org.chatRoom.core.event.user

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.InstantSerializer
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Id
import java.time.Instant

@Serializable
data class ChangeEmail(
    override val eventId: Id = Id(),
    override val modelId: Id,
    @Serializable(with = InstantSerializer::class)
    override val dateIssued: Instant = Instant.now(),
    val email: EmailAddress,
) : UserEvent {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val eventType = Companion.eventType

    companion object {
        const val eventType = "user:change-email"
    }

    init {
        if (eventType != Companion.eventType) error("Event type mismatch")
    }
}
