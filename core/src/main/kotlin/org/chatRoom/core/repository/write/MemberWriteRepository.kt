package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Member

interface MemberWriteRepository {
    fun create(member: Member)
    fun update(member: Member)
    fun delete(member: Member)
}
