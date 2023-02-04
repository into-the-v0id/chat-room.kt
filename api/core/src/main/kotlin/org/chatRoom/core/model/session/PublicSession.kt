package org.chatRoom.core.model.session

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.Session as SessionAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class PublicSession(
    val id: Id,
    val userId: Id,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateValidUntil: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
) {
    constructor(session: SessionAggregate) : this(
        id = session.modelId,
        userId = session.userId,
        dateValidUntil = session.dateValidUntil.atOffset(ZoneOffset.UTC),
        dateCreated = session.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
