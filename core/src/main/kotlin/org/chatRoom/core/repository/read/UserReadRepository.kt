package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.UserSortCriterion

interface UserReadRepository {
    fun getById(id: Id): User?
    fun getAll(
        ids: List<Id>? = null,
        handles: List<Handle>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
        sortCriteria: List<UserSortCriterion> = listOf(),
    ): Collection<User>
}
