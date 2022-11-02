package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@JvmInline
value class Id(private val id: String) {
    constructor(): this(UUID.randomUUID().toString())

    init {
        // validate UUID
        UUID.fromString(id)
    }

    override fun toString(): String = id

    fun toUuid(): UUID = UUID.fromString(id)
}
