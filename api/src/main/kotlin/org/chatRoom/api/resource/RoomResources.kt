package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id

@Serializable
@Resource("rooms")
class Rooms {
    @Serializable
    @Resource("{id}")
    class Detail(val parent: Rooms = Rooms(), val id: Id)
}
