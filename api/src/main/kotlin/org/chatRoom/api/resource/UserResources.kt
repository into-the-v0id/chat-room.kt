package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
@Resource("users")
class Users {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Users = Users()
    }
}
