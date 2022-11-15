package org.chatRoom.core.payload.user

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator
import org.chatRoom.core.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime

@Serializable
data class UpdateUser(
    val handle: Handle,
    val email: String,
) {
    val id: Id? = null
    @Serializable(with = OffsetDateTimeSerializer::class)
    private val dateCreated: OffsetDateTime? = null
    @Serializable(with = OffsetDateTimeSerializer::class)
    private val dateUpdated: OffsetDateTime? = null

    init {
        require(EmailValidator.getInstance().isValid(email)) { "Invalid email" }
    }
}
