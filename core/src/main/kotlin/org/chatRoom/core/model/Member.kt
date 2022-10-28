package org.chatRoom.core.model

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.Member as MemberAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class Member(
    val id: Id,
    val userId: Id,
    val roomId: Id,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateUpdated: OffsetDateTime,
) {
    constructor(member: MemberAggregate) : this(
        id = member.modelId,
        userId = member.userId,
        roomId = member.roomId,
        dateCreated = member.dateCreated.atOffset(ZoneOffset.UTC),
        dateUpdated = member.dateUpdated.atOffset(ZoneOffset.UTC),
    )
}
