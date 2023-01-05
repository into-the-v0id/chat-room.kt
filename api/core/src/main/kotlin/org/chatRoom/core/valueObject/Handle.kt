package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Handle(private val handle: String) {
    init {
        require(handle.matches(Regex("^[a-zA-Z0-9\\-_]+\$"))) { "Invalid handle" }
        require(handle.length <= 50) { "Invalid handle" }
    }

    override fun toString(): String = handle
}
