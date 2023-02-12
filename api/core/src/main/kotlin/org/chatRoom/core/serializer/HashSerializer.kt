package org.chatRoom.core.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.chatRoom.core.valueObject.Hash

class HashSerializer : KSerializer<Hash> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Hash", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Hash) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Hash = Hash.fromHash(decoder.decodeString())
}
