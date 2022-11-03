package org.chatRoom.core.valueObject

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderDirection {
    @SerialName("asc")
    ASC,
    @SerialName("desc")
    DESC,
}
