package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset

@Serializable
@Resource("users")
class Users(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("handle")
    val handles: List<Handle> = listOf(),
    val offset: Offset? = null,
    val limit: Limit? = null,
) {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Users = Users()
    }
}
