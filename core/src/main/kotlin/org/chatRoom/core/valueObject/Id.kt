package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@JvmInline
value class Id(private val id: String) {
    companion object {
        fun tryFrom(id: String): Id? {
            return try {
                Id(id)
            } catch (e: Throwable) {
                null
            }
        }
    }

    constructor(): this(UUID.randomUUID().toString())

    init {
        // validate UUID
        UUID.fromString(id)
    }

    override fun toString(): String = id
}
