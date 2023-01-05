package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.room.RoomSortCriterion

interface RoomReadRepository {
    fun getById(id: Id): Room?
    fun getAll(
        ids: List<Id>? = null,
        handles: List<Handle>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
        sortCriteria: List<RoomSortCriterion> = listOf(),
    ): Collection<Room>
}
