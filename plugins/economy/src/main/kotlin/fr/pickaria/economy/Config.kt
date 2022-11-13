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

	val cantSendToYourself by miniMessage
	val playerDoesNotExist by miniMessage
	val amountIsNan by miniMessage
	val lessThanMinimumAmount by miniMessage
	val receiveError by miniMessage
	val refundError by miniMessage
	val sendSuccess: String by this
	val receiveSuccess: String by this
	val sendError by miniMessage
	val notEnoughMoney by miniMessage

	val notMuchPages by miniMessage
	val header: String by this
	val row: String by this
	val footer: String by this
}