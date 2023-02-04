package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Serializable
@JvmInline
value class Password private constructor(private val hash: String) {
    companion object {
        private val encoder: PasswordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

        fun create(rawPassword: String): Password = Password(encoder.encode(rawPassword))

        fun fromHash(hash: String): Password = Password(hash)
    }

    init {
        require(hash.startsWith("\$argon2")) { "Invalid password hash" }
    }

    fun verify(rawPassword: String): Boolean = encoder.matches(rawPassword, hash)

    override fun toString(): String = hash
}
