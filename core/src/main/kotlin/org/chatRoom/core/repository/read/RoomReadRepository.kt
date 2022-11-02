package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset

interface RoomReadRepository {
    fun getById(id: Id): Room?
    fun getAll(
        ids: List<Id>? = null,
        handles: List<Handle>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
    ): Collection<Room>
}
