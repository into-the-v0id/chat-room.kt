package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.room.RoomSortCriterion

@Serializable
@Resource("rooms")
class Rooms(
    @SerialName("id")
    val ids: List<Id> = listOf(),
    @SerialName("handle")
    val handles: List<Handle> = listOf(),
    val offset: Offset? = null,
    val limit: Limit? = null,
    @SerialName("sort_criteria")
    val sortCriteria: List<RoomSortCriterion> = listOf(),
) {
    @Serializable
    @Resource("{id}")
    class Detail(val id: Id) {
        val parent: Rooms = Rooms()
    }
}
