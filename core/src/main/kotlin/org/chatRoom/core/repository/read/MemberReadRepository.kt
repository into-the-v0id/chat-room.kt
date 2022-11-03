package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.member.MemberSortCriterion

interface MemberReadRepository {
    fun getById(id: Id): Member?
    fun getAll(
        ids: List<Id>? = null,
        userIds: List<Id>? = null,
        roomIds: List<Id>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
        sortCriteria: List<MemberSortCriterion> = listOf(),
    ): Collection<Member>
}
