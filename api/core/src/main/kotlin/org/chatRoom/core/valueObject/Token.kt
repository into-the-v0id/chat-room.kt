package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable
import java.security.SecureRandom

@Serializable
@JvmInline
value class Token(private val token: String) {
    constructor(): this(generate())

    companion object {
        private fun generate(): String {
            val data = ByteArray(64)
            SecureRandom().nextBytes(data)
            return data.toHex()
        }
    }

    init {
        require(token.matches(Regex("^[0-9a-fA-F]{128}\$"))) { "Invalid token" }
    }

    override fun toString(): String = token
}

private fun ByteArray.toHex(): String = joinToString(separator = "") { byte -> "%02x".format(byte) }
