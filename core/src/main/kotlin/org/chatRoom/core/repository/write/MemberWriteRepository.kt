package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Member

interface MemberWriteRepository {
    fun createAll(members: Collection<Member>)
    fun updateAll(members: Collection<Member>)
    fun deleteAll(members: Collection<Member>)
}

fun MemberWriteRepository.create(member: Member) = createAll(listOf(member))
fun MemberWriteRepository.update(member: Member) = updateAll(listOf(member))
fun MemberWriteRepository.delete(member: Member) = deleteAll(listOf(member))
