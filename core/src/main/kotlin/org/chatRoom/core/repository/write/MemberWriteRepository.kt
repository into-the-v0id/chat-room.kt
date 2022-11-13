package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface MemberWriteRepository {
    fun createAll(members: Collection<Member>, transaction: Transaction)
    fun updateAll(members: Collection<Member>, transaction: Transaction)
    fun deleteAll(members: Collection<Member>, transaction: Transaction)
}

fun MemberWriteRepository.createAll(members: Collection<Member>) = Transaction(id = Id()).execute { transaction ->
    createAll(members, transaction)
}
fun MemberWriteRepository.updateAll(members: Collection<Member>) = Transaction(id = Id()).execute { transaction ->
    updateAll(members, transaction)
}
fun MemberWriteRepository.deleteAll(members: Collection<Member>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(members, transaction)
}
fun MemberWriteRepository.create(member: Member, transaction: Transaction) = createAll(listOf(member), transaction)
fun MemberWriteRepository.update(member: Member, transaction: Transaction) = updateAll(listOf(member), transaction)
fun MemberWriteRepository.delete(member: Member, transaction: Transaction) = deleteAll(listOf(member), transaction)
fun MemberWriteRepository.create(member: Member) = createAll(listOf(member))
fun MemberWriteRepository.update(member: Member) = updateAll(listOf(member))
fun MemberWriteRepository.delete(member: Member) = deleteAll(listOf(member))
