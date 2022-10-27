package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.valueObject.Id

interface MessageReadRepository {
    fun getById(id: Id): Message?
    fun getAll(ids: List<Id>? = null, memberIds: List<Id>? = null): Collection<Message>
}
