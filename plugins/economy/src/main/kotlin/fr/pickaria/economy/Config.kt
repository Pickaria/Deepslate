package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.sound.Sound


internal object Config : ConfigProvider() {
	val currencyNameSingular: String by this
	val currencyNamePlural: String by this
	val currencyDescription: String by this
	val currencyCollectMessage: String by this
	val currencyCollectSound: Sound by this

	val balanceMessage: String by this

	val cantSendToYourself by miniMessageDeserializer
	val playerDoesNotExist by miniMessageDeserializer
	val amountIsNan by miniMessageDeserializer
	val lessThanMinimumAmount by miniMessageDeserializer
	val receiveError by miniMessageDeserializer
	val refundError by miniMessageDeserializer
	val sendSuccess: String by this
	val receiveSuccess: String by this
	val sendError by miniMessageDeserializer
	val notEnoughMoney by miniMessageDeserializer

	val notMuchPages by miniMessageDeserializer
	val header: String by this
	val row: String by this
	val footer: String by this
	val coinDescription: String by this
	val coinCollectMessage: String by this
	val coinCollectSound: String by this
}