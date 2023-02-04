package org.chatRoom.core.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.chatRoom.core.valueObject.session.SessionToken

class SessionTokenSerializer : KSerializer<SessionToken> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SessionToken", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SessionToken) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): SessionToken = SessionToken.parse(decoder.decodeString())
}
