package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset

@Serializable
@Resource("messages")
class Messages(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("member_id")
    val memberIds: List<Id> = listOf(),
    @SerialName("room_id")
    val roomIds: List<Id> = listOf(),
    val offset: Offset? = null,
    val limit: Limit? = null,
) {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Messages = Messages()
    }
}
