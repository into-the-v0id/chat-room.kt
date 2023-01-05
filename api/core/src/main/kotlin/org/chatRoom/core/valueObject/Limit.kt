package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Limit(private val limit: Int) {
    init {
        require(limit > 0) { "Limit must be a positive number" }
    }

    override fun toString(): String = limit.toString()

    fun toInt(): Int = limit
}
