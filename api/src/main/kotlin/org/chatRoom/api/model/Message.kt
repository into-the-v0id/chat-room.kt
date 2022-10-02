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
) {
    constructor(message: MessageAggregate) : this(
        message.modelId,
        message.memberId,
        message.content,
        message.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
