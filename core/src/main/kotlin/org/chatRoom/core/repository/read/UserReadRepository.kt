package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.valueObject.Id

interface UserReadRepository {
    fun getById(id: Id): User?
    fun getAll(ids: List<Id>? = null, handles: List<Handle>? = null): Collection<User>
}
