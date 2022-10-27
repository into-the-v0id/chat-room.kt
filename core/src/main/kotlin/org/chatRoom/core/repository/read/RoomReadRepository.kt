package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Room
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id

interface RoomReadRepository {
    fun getById(id: Id): Room?
    fun getAll(ids: List<Id>? = null, handles: List<Handle>? = null): Collection<Room>
}
