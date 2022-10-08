package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id

@Serializable
@Resource("users")
class Users(
    @SerialName("handle")
    val handles: List<Handle> = listOf(),
) {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Users = Users()
    }
}
