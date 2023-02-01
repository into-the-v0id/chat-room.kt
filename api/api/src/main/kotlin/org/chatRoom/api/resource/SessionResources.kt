package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.session.SessionSortCriterion

@Serializable
@Resource("sessions")
class Sessions(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("user_id")
    val userIds: List<Id> = listOf(),
    val offset: Offset? = null,
    val limit: Limit? = null,
    @SerialName("sort_criteria")
    val sortCriteria: List<SessionSortCriterion> = listOf(),
) {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Sessions = Sessions()
    }
}
