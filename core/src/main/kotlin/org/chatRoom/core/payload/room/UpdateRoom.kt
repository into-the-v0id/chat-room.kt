package org.chatRoom.core.payload.room

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime

@Serializable
data class UpdateRoom(
    val id: Id,
    val handle: Handle,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateUpdated: OffsetDateTime,
)