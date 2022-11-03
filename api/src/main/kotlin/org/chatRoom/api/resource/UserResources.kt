package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.OrderBy

@Serializable
@Resource("users")
class Users(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("handle")
    val handles: List<Handle> = listOf(),
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
        val parent: Users = Users()
    }
}
