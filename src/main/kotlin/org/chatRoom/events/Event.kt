package org.chatRoom.events

import org.chatRoom.valueObject.Id
import java.util.Date

interface Event {
    val eventId: Id
    val modelId: Id
    val dateIssued: Date
}
