package fr.pickaria.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.block.banner.PatternType


object PatternTypeSerializer : KSerializer<PatternType> {
	override val descriptor = PrimitiveSerialDescriptor("PatternType", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): PatternType {
		return PatternType.getByIdentifier(decoder.decodeString())!!
	}

	override fun serialize(encoder: Encoder, value: PatternType) {
		encoder.encodeString(value.identifier)
	}
}