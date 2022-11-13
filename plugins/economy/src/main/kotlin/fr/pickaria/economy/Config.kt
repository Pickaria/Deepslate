package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider


internal object Config : ConfigProvider() {
	val currencyNameSingular: String by this
	val currencyNamePlural: String by this
	val balanceMessage: String by this
	val cantSendToYourself by this.miniMessage
	val playerDoesNotExist by this.miniMessage
	val amountIsNan by this.miniMessage
	val lessThanMinimumAmount by this.miniMessage
	val receiveError by this.miniMessage
	val refundError by this.miniMessage
	val sendSuccess: String by this
	val receiveSuccess: String by this
	val sendError by this.miniMessage
	val notEnoughMoney by this.miniMessage
	val notMuchPages by this.miniMessage
	val header: String by this
	val row: String by this
	val footer: String by this
	val currencyDescription: String by this
	val currencyCollectMessage: String by this
	val currencyCollectSound: String by this
}