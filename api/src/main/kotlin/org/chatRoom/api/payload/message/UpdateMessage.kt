package org.chatRoom.api.payload.message

import kotlinx.serialization.Serializable
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime

@Serializable
data class UpdateMessage(
    val id: Id,
    val memberId: Id,
    val content: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
)
