package org.chatRoom.core.repository.read

import org.chatRoom.core.aggreagte.Message
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset

interface MessageReadRepository {
    fun getById(id: Id): Message?
    fun getAll(
        ids: List<Id>? = null,
        memberIds: List<Id>? = null,
        offset: Offset? = null,
        limit: Limit? = null,
    ): Collection<Message>
}
