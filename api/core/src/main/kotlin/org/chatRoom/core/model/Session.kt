package org.chatRoom.core.model

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.Session as SessionAggregate
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Token
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class Session(
    val id: Id,
    val userId: Id,
    val token: Token,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateValidUntil: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
) {
    constructor(session: SessionAggregate) : this(
        id = session.modelId,
        userId = session.userId,
        token = session.token,
        dateValidUntil = session.dateValidUntil.atOffset(ZoneOffset.UTC),
        dateCreated = session.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
