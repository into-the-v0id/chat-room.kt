package org.chatRoom.core.valueObject.session

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.chatRoom.core.serializer.SessionTokenSerializer
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.valueObject.Token
import java.util.Base64
import org.chatRoom.core.aggreagte.Session as SessionAggregate

@Serializable(with = SessionTokenSerializer::class)
class SessionToken private constructor(private val data: SessionTokenData) {
    companion object {
        fun parse(token: String): SessionToken {
            val binaryData = try {
                Base64.getDecoder().decode(token)
            } catch (base64Exception: Throwable) {
                val tokenException = IllegalArgumentException("Invalid session token: Invalid base64")
                tokenException.addSuppressed(base64Exception)
                throw tokenException
            }
            require(binaryData != null) { "Invalid session token: Invalid base64" }

            val stringData = try {
                String(binaryData)
            } catch (stringException: Throwable) {
                val tokenException = IllegalArgumentException("Invalid session token: Invalid UTF-8 ")
                tokenException.addSuppressed(stringException)
                throw tokenException
            }

            val data = try {
                Json.decodeFromString<SessionTokenData>(stringData)
            } catch (jsonException: Throwable) {
                val tokenException = IllegalArgumentException("Invalid session token: Invalid JSON")
                tokenException.addSuppressed(jsonException)
                throw tokenException
            }

            return SessionToken(data)
        }
    }

    constructor(id: Id, secret: Token) : this(SessionTokenData(id, secret))

    val id: Id
        get() = data.id

    val secret: Token
        get() = data.secret

    override fun hashCode(): Int = data.hashCode()

    override fun equals(other: Any?): Boolean = other is SessionToken && other.data == data

    override fun toString(): String {
        val jsonData = Json.encodeToString(data)
        val base64Data = Base64.getEncoder().encodeToString(jsonData.encodeToByteArray())

        return base64Data
    }
}

@Serializable
private data class SessionTokenData(
    val id: Id,
    val secret: Token,
) {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    val type: String = "session"

    init {
        require(type == "session") { "Invalid session token data: Mismatching type" }
    }
}
