package org.chatRoom.core.repository.read

import kotlinx.serialization.Serializable
import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.room.RoomSortCriterion

@Serializable
data class RoomQuery(
    val ids: List<Id>? = null,
    val handles: List<Handle>? = null,
    val offset: Offset? = null,
    val limit: Limit? = null,
    val sortCriteria: List<RoomSortCriterion> = listOf(),
)

interface RoomReadRepository {
    fun getById(id: Id): Room?
    fun getAll(query: RoomQuery = RoomQuery()): Collection<Room>
    fun count(query: RoomQuery = RoomQuery()): Int
}
