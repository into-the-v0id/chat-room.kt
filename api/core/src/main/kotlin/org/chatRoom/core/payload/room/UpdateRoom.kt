package org.chatRoom.core.payload.room

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime

@Serializable
data class UpdateRoom(
    val handle: Handle,
) {
    val id: Id? = null
    @Serializable(with = OffsetDateTimeSerializer::class)
    private val dateCreated: OffsetDateTime? = null
    @Serializable(with = OffsetDateTimeSerializer::class)
    private val dateUpdated: OffsetDateTime? = null
}
