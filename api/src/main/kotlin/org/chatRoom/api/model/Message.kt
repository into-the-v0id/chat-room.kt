package org.chatRoom.api.model

import kotlinx.serialization.Serializable
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.Message as MessageAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class Message(
    val id: Id,
    val memberId: Id,
    val content: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateUpdated: OffsetDateTime,
) {
    constructor(message: MessageAggregate) : this(
        id = message.modelId,
        memberId = message.memberId,
        content = message.content,
        dateCreated = message.dateCreated.atOffset(ZoneOffset.UTC),
        dateUpdated = message.dateUpdated.atOffset(ZoneOffset.UTC),
    )
}
