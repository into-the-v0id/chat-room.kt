package org.chatRoom.core.event.user

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.InstantSerializer
import org.chatRoom.core.valueObject.Id
import java.time.Instant

@Serializable
data class DeleteUser(
    override val eventId: Id = Id(),
    override val modelId: Id,
    @Serializable(with = InstantSerializer::class)
    override val dateIssued: Instant = Instant.now(),
) : UserEvent {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    override val eventType = Companion.eventType

    companion object {
        const val eventType = "user:delete"
    }

    init {
        if (eventType != Companion.eventType) error("Event type mismatch")
    }
}
