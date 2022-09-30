package org.chatRoom.core.event

import org.chatRoom.core.valueObject.Id
import java.util.Date

interface Event {
    val eventId: Id
    val modelId: Id
    val dateIssued: Date
}
