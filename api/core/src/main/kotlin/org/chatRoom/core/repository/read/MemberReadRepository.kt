package org.chatRoom.core.repository.read

import kotlinx.serialization.Serializable
import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.member.MemberSortCriterion

@Serializable
data class MemberQuery(
    val ids: List<Id>? = null,
    val userIds: List<Id>? = null,
    val roomIds: List<Id>? = null,
    val offset: Offset? = null,
    val limit: Limit? = null,
    val sortCriteria: List<MemberSortCriterion> = listOf(),
)

interface MemberReadRepository {
    fun getById(id: Id): Member?
    fun getAll(query: MemberQuery = MemberQuery()): Collection<Member>
    fun count(query: MemberQuery = MemberQuery()): Int
}
