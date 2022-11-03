package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.OrderDirection
import org.chatRoom.core.valueObject.member.OrderBy

@Serializable
@Resource("members")
class Members(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("user_id")
    val userIds: List<Id> = listOf(),
    @SerialName("room_id")
    val roomIds: List<Id> = listOf(),
    val offset: Offset? = null,
    val limit: Limit? = null,
    @SerialName("order_by")
    val orderBy: OrderBy? = null,
    @SerialName("order_direction")
    val orderDirection: OrderDirection? = null,
) {
    init {
        if (orderDirection != null) require(orderBy != null) { "Incomplete order" }
    }

    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Members = Members()
    }
}
