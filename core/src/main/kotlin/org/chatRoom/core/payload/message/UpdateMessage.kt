package org.chatRoom.core.payload.message

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime

@Serializable
data class UpdateMessage(
    val content: String,
) {
    val id: Id? = null
    val memberId: Id? = null
    @Serializable(with = OffsetDateTimeSerializer::class)
    private val dateCreated: OffsetDateTime? = null
    @Serializable(with = OffsetDateTimeSerializer::class)
    private val dateUpdated: OffsetDateTime? = null
}
