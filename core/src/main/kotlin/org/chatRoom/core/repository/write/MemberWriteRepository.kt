package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Member

interface MemberWriteRepository {
    fun createAll(members: List<Member>)
    fun updateAll(members: List<Member>)
    fun deleteAll(members: List<Member>)
}

fun MemberWriteRepository.create(member: Member) = createAll(listOf(member))
fun MemberWriteRepository.update(member: Member) = updateAll(listOf(member))
fun MemberWriteRepository.delete(member: Member) = deleteAll(listOf(member))
