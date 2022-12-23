package fr.pickaria.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage

object SoundSerializable : KSerializer<Sound> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Sound", PrimitiveKind.STRING)
	private val miniMessage = MiniMessage.miniMessage()

	override fun serialize(encoder: Encoder, value: Sound) {
		encoder.encodeString(value.name().value())
	}

	override fun deserialize(decoder: Decoder): Sound {
		return Sound.sound(Key.key(decoder.decodeString()), Sound.Source.MASTER, 1f, 1f)
	}
}