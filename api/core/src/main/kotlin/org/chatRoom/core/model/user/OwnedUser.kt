package org.chatRoom.core.model.user

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.EmailAddress
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.aggreagte.User as UserAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class OwnedUser(
    val id: Id,
    val handle: Handle,
    val email: EmailAddress,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateUpdated: OffsetDateTime,
) {
    constructor(user: UserAggregate) : this(
        id = user.modelId,
        handle = user.handle,
        email = user.email,
        dateCreated = user.dateCreated.atOffset(ZoneOffset.UTC),
        dateUpdated = user.dateUpdated.atOffset(ZoneOffset.UTC),
    )
}
