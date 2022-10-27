package org.chatRoom.api.model

import kotlinx.serialization.Serializable
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.valueObject.Handle
import org.chatRoom.core.aggreagte.Room as RoomAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class Room(
    val id: Id,
    val handle: Handle,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateUpdated: OffsetDateTime,
) {
    constructor(room: RoomAggregate) : this(
        id = room.modelId,
        handle = room.handle,
        dateCreated = room.dateCreated.atOffset(ZoneOffset.UTC),
        dateUpdated = room.dateUpdated.atOffset(ZoneOffset.UTC),
    )
}
