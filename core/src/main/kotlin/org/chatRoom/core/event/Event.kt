package org.chatRoom.core.event

import org.chatRoom.core.valueObject.Id
import java.time.Instant

interface Event {
    val eventId: Id
    val modelId: Id
    val dateIssued: Instant
}
