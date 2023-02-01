package org.chatRoom.core.event.session

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.InstantSerializer
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Token
import java.time.Instant

@Serializable
data class CreateSession(
    override val eventId: Id = Id(),
    override val modelId: Id,
    @Serializable(with = InstantSerializer::class)
    override val dateIssued: Instant = Instant.now(),
    val userId: Id,
    val token: Token,
    @Serializable(with = InstantSerializer::class)
    val dateValidUntil: Instant,
) : SessionEvent {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val eventType = Companion.eventType

    companion object {
        const val eventType = "session:create"
    }

    init {
        if (eventType != Companion.eventType) error("Event type mismatch")
    }
}
