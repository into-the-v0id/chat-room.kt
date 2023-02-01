package org.chatRoom.core.valueObject.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SessionSortCriterion {
    @SerialName("dateValidUntilAsc")
    DATE_VALID_UNTIL_ASC,
    @SerialName("dateValidUtilDesc")
    DATE_VALID_UNTIL_DESC,

    @SerialName("dateCreatedAsc")
    DATE_CREATED_ASC,
    @SerialName("dateCreatedDesc")
    DATE_CREATED_DESC,
}
