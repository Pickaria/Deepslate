package fr.pickaria.model.economy

import fr.pickaria.controller.economy.CurrencyController
import fr.pickaria.controller.economy.Economy
import fr.pickaria.model.serializers.SoundSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound

@Serializable
data class Currency(
	val description: List<String>,

	@SerialName("name_singular")
	val nameSingular: String,

	@SerialName("name_plural")
	val namePlural: String,
	val account: String,
	val format: String,

	@SerialName("collect_message")
	val collectMessage: String,

	@Serializable(with = SoundSerializer::class)
	@SerialName("collect_sound")
	val collectSound: Sound,

	@SerialName("physical_currencies")
	val physicalCurrencies: List<PhysicalCurrency>,

	val advancements: List<CurrencyAdvancement>? = null,
) {
	@Deprecated("Prefer using the CurrencyController instead.")
	val economy by lazy {
		Economy(nameSingular, namePlural, account, format)
	}
}

/**
 * Get a controller for this currency.
 */
fun Currency.toController(): CurrencyController = CurrencyController(this)