package org.chatRoom.core.repository.write

import org.chatRoom.core.aggreagte.Member
import org.chatRoom.core.repository.Transaction
import org.chatRoom.core.repository.execute
import org.chatRoom.core.valueObject.Id

interface MemberWriteRepository {
    suspend fun createAll(members: Collection<Member>, transaction: Transaction)
    suspend fun updateAll(members: Collection<Member>, transaction: Transaction)
    suspend fun deleteAll(members: Collection<Member>, transaction: Transaction)
}

suspend fun MemberWriteRepository.createAll(members: Collection<Member>) = Transaction(id = Id()).execute { transaction ->
    createAll(members, transaction)
}
suspend fun MemberWriteRepository.updateAll(members: Collection<Member>) = Transaction(id = Id()).execute { transaction ->
    updateAll(members, transaction)
}
suspend fun MemberWriteRepository.deleteAll(members: Collection<Member>) = Transaction(id = Id()).execute { transaction ->
    deleteAll(members, transaction)
}
suspend fun MemberWriteRepository.create(member: Member, transaction: Transaction) = createAll(listOf(member), transaction)
suspend fun MemberWriteRepository.update(member: Member, transaction: Transaction) = updateAll(listOf(member), transaction)
suspend fun MemberWriteRepository.delete(member: Member, transaction: Transaction) = deleteAll(listOf(member), transaction)
suspend fun MemberWriteRepository.create(member: Member) = createAll(listOf(member))
suspend fun MemberWriteRepository.update(member: Member) = updateAll(listOf(member))
suspend fun MemberWriteRepository.delete(member: Member) = deleteAll(listOf(member))
