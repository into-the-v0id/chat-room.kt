package org.chatRoom.api.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime

@Serializable
data class UpdateUser(
    val id: Id,
    val handle: Handle,
    val email: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateUpdated: OffsetDateTime,
) {
    init {
        if (! EmailValidator.getInstance().isValid(email)) error("Invalid email")
    }
}
