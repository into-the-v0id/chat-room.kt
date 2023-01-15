package org.chatRoom.core.repository.read

import kotlinx.serialization.Serializable
import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.user.UserSortCriterion

@Serializable
data class UserQuery(
    val ids: List<Id>? = null,
    val handles: List<Handle>? = null,
    val offset: Offset? = null,
    val limit: Limit? = null,
    val sortCriteria: List<UserSortCriterion> = listOf(),
)

interface UserReadRepository {
    fun getById(id: Id): User?
    fun getAll(query: UserQuery = UserQuery()): Collection<User>
    fun count(query: UserQuery = UserQuery()): Int
}
