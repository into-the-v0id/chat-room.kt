package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.UserSortCriterion

@Serializable
@Resource("users")
class Users(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("handle")
    val handles: List<Handle> = listOf(),
    @SerialName("email")
    val emails: List<EmailAddress> = listOf(),
    val offset: Offset? = null,
    val limit: Limit? = null,
    @SerialName("sort_criteria")
    val sortCriteria: List<UserSortCriterion> = listOf(),
) {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Users = Users()
    }
}
