package org.chatRoom.core.valueObject.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MemberSortCriterion {
    @SerialName("dateCreatedAsc")
    DATE_CREATED_ASC,
    @SerialName("dateCreatedDesc")
    DATE_CREATED_DESC,

    @SerialName("dateUpdatedAsc")
    DATE_UPDATED_ASC,
    @SerialName("dateUpdatedDesc")
    DATE_UPDATED_DESC,
}
