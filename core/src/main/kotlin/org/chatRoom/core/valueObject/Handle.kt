package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Handle(private val handle: String) {
    init {
        if (! handle.matches(Regex("^[a-zA-Z0-9\\-_]+\$"))) error("Invalid handle")
        if (handle.length > 50) error("Invalid handle")
    }

    override fun toString(): String = handle
}
