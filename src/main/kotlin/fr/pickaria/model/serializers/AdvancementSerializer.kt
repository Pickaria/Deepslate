package fr.pickaria.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.advancement.Advancement

object AdvancementSerializer : KSerializer<Advancement> {
	override val descriptor = PrimitiveSerialDescriptor("Advancement", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): Advancement {
		val key = try {
			NamespacedKey.fromString(decoder.decodeString())!!
		} catch (_: NullPointerException) {
			throw RuntimeException("Cannot decode namespaced key")
		}
		return try {
			Bukkit.getAdvancement(key)!!
		} catch (_: NullPointerException) {
			throw RuntimeException("Advancement ${key.asString()} not found")
		}
	}

	override fun serialize(encoder: Encoder, value: Advancement) {
		encoder.encodeString(value.key.namespace)
	}
}