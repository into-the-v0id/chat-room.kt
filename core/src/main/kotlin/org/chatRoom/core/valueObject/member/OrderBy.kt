package org.chatRoom.core.valueObject.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderBy {
    @SerialName("dateCreated")
    DATE_CREATED,
    @SerialName("dateUpdated")
    DATE_UPDATED,
}
