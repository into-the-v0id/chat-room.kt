package org.chatRoom.api.model

import kotlinx.serialization.Serializable
import org.chatRoom.core.serializer.DateSerializer
import org.chatRoom.core.aggreagte.User as UserAggregate
import org.chatRoom.core.valueObject.Id
import java.util.*

@Serializable
data class User(
    val id: Id,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    @Serializable(with = DateSerializer::class)
    val dateCreated: Date,
) {
    constructor(user: UserAggregate) : this(
        user.modelId,
        user.email,
        user.firstName,
        user.lastName,
        user.dateCreated,
    )
}
