package org.chatRoom.api.model

import kotlinx.serialization.Serializable
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.User as UserAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class User(
    val id: Id,
    val email: String,
    val handle: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
) {
    constructor(user: UserAggregate) : this(
        id = user.modelId,
        email = user.email,
        handle = user.handle,
        dateCreated = user.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
