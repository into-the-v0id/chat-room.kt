package org.chatRoom.core.repository.read

import kotlinx.serialization.Serializable
import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.valueObject.message.MessageSortCriterion

@Serializable
data class MessageQuery(
    val ids: List<Id>? = null,
    val memberIds: List<Id>? = null,
    val offset: Offset? = null,
    val limit: Limit? = null,
    val sortCriteria: List<MessageSortCriterion> = listOf(),
)

interface MessageReadRepository {
    fun getById(id: Id): Message?
    fun getAll(query: MessageQuery = MessageQuery()): Collection<Message>
    fun count(query: MessageQuery = MessageQuery()): Int
}
