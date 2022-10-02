package org.chatRoom.api.model

import kotlinx.serialization.Serializable
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
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
) {
    constructor(member: MemberAggregate) : this(
        member.modelId,
        member.userId,
        member.roomId,
        member.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
