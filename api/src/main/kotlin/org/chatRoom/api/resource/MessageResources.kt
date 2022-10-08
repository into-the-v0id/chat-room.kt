package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
@Resource("messages")
class Messages {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Messages = Messages()
    }
}
