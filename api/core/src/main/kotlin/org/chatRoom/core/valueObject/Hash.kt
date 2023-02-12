package org.chatRoom.core.valueObject

import kotlinx.serialization.Serializable
import org.bouncycastle.util.encoders.Hex
import org.chatRoom.core.serializer.HashSerializer
import java.security.MessageDigest

private enum class HashAlgorithm(val handle: String) {
    SHA256("sha256"),
}

@Serializable(with = HashSerializer::class)
class Hash private constructor(
    private val algorithm: HashAlgorithm,
    private val hash: String,
) {
    companion object {
        fun create(rawValue: String): Hash = Hash(
            HashAlgorithm.SHA256,
            rawValue.hash(HashAlgorithm.SHA256)
        )

        fun fromHash(composedHash: String): Hash = when {
            composedHash.startsWith("${ HashAlgorithm.SHA256.handle }-") -> Hash(
                HashAlgorithm.SHA256,
                composedHash.removePrefix("${ HashAlgorithm.SHA256.handle }-")
            )
            else -> throw IllegalArgumentException("Invalid composed hash")
        }
    }

    init {
        when (algorithm) {
            HashAlgorithm.SHA256 -> require(hash.matches(Regex("^[0-9a-fA-F]{64}\$"))) { "Invalid sha256 hash" }
        }
    }

    fun verify(rawValue: String): Boolean {
        val otherHash = rawValue.hash(algorithm)

        return otherHash.lowercase() == hash.lowercase()
    }

    override fun hashCode(): Int = toString().hashCode()

    override fun equals(other: Any?): Boolean = other is Hash && other.toString() == toString()

    override fun toString(): String = "${ algorithm.handle }-${ hash }"
}

private fun String.hash(algorithm: HashAlgorithm): String {
    val digest: MessageDigest = when (algorithm) {
        HashAlgorithm.SHA256 -> MessageDigest.getInstance("SHA-256")!!
    }

    val stringBytes = encodeToByteArray()
    val hashBytes = digest.digest(stringBytes)
    val hash = Hex.toHexString(hashBytes)

    return hash
}
