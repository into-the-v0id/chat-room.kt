package org.chatRoom.core.model.session

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.Session as SessionAggregate
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Token
import org.chatRoom.core.valueObject.session.SessionToken
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class CreatedSession(
    val id: Id,
    val userId: Id,
    val token: SessionToken,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateValidUntil: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
) {
    constructor(session: SessionAggregate, secret: Token) : this(
        id = session.modelId,
        userId = session.userId,
        token = SessionToken(session.modelId, secret),
        dateValidUntil = session.dateValidUntil.atOffset(ZoneOffset.UTC),
        dateCreated = session.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
