package fr.pickaria.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.loot.LootTable

object LootTableSerializer : KSerializer<LootTable> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LootTable", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: LootTable) {
		encoder.encodeString(value.key.key)
	}

	override fun deserialize(decoder: Decoder): LootTable {
		val namespace = NamespacedKey.fromString(decoder.decodeString())!!
		return Bukkit.getLootTable(namespace)!!
	}
}