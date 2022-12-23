package fr.pickaria.model.economy

import fr.pickaria.controller.economy.Economy
import fr.pickaria.model.serializers.SoundSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import java.util.*

@Serializable
data class Currency(
	val material: Material,
	val description: List<String>,

	@SerialName("name_singular")
	val nameSingular: String,

	@SerialName("name_plural")
	val namePlural: String,
	val account: String,
	val format: String,

	@SerialName("collect_message")
	val collectMessage: String,

	@Serializable(with = SoundSerializable::class)
	@SerialName("collect_sound")
	val collectSound: Sound,

	@SerialName("vault_support")
	val vaultSupport: Boolean? = false,
) {
	val economy by lazy {
		Economy(nameSingular, namePlural, account, format)
	}
}