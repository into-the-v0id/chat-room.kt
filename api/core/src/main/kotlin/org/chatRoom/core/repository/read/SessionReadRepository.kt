package org.chatRoom.core.repository.read

import kotlinx.serialization.Serializable
import org.chatRoom.core.aggreagte.Session
import org.chatRoom.core.valueObject.*
import org.chatRoom.core.valueObject.session.SessionSortCriterion

@Serializable
data class SessionQuery(
    val ids: List<Id>? = null,
    val userIds: List<Id>? = null,
    val offset: Offset? = null,
    val limit: Limit? = null,
    val sortCriteria: List<SessionSortCriterion> = listOf(),
)

interface SessionReadRepository {
    fun getById(id: Id): Session?
    fun getAll(query: SessionQuery = SessionQuery()): Collection<Session>
    fun count(query: SessionQuery = SessionQuery()): Int
}
