package org.chatRoom.valueObject

import java.util.UUID

@JvmInline
value class Id(private val id: String) {
    constructor(): this(UUID.randomUUID().toString())

    init {
        // validate UUID
        UUID.fromString(id)
    }

    override fun toString(): String = id
}
