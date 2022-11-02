package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Offset(private val offset: Int) {
    init {
        require(offset > 0) { "Offset must be a positive number" }
    }

    override fun toString(): String = offset.toString()

    fun toInt(): Int = offset
}
