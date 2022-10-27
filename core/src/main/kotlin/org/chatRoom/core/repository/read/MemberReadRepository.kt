package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.valueObject.Id

interface MemberReadRepository {
    fun getById(id: Id): Member?
    fun getAll(ids: List<Id>? = null, userIds: List<Id>? = null, roomIds: List<Id>? = null): Collection<Member>
}
