package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
@Resource("members")
class Members {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Members = Members()
    }
}
