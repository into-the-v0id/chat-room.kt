package org.chatRoom.core.valueObject

import com.github.f4b6a3.uuid.UuidCreator
import com.github.f4b6a3.uuid.util.UuidValidator
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@JvmInline
value class Id(private val id: String) {
    constructor(): this(UuidCreator.getTimeOrderedEpoch().toString())

    init {
        require(UuidValidator.isValid(id)) { "Invalid UUID" }
    }

    override fun toString(): String = id

    fun toUuid(): UUID = UUID.fromString(id)
}
