package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset

interface UserReadRepository {
    fun getById(id: Id): User?
    fun getAll(
        ids: List<Id>? = null,
        handles: List<Handle>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
    ): Collection<User>
}
