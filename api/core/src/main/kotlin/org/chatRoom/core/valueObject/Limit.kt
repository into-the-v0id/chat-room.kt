package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Limit(private val limit: Int) {
    init {
        require(limit > 0) { "Limit must be greater than 0" }
        require(limit <= 100) { "Limit must be less than or equal to 100" }
    }

    override fun toString(): String = limit.toString()

    fun toInt(): Int = limit
}
