package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.OrderBy

interface UserReadRepository {
    fun getById(id: Id): User?
    fun getAll(
        ids: List<Id>? = null,
        handles: List<Handle>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
        orderBy: OrderBy? = null,
        orderDirection: OrderDirection? = null,
    ): Collection<User>
}
