package org.chatRoom.api.model

import kotlinx.serialization.Serializable
import org.chatRoom.api.serializer.OffsetDateTimeSerializer
import org.chatRoom.core.aggreagte.Room as RoomAggregate
import org.chatRoom.core.valueObject.Id
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class Room(
    val id: Id,
    val handle: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
) {
    constructor(room: RoomAggregate) : this(
        room.modelId,
        room.handle,
        room.dateCreated.atOffset(ZoneOffset.UTC),
    )
}
