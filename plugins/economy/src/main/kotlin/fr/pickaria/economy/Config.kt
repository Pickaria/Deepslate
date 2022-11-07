package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider


internal object Config : ConfigProvider() {
	val currencyNameSingular: String by this
	val currencyNamePlural: String by this
	val balanceMessage: String by this
	val cantSendToYourself by this.miniMessageDeserializer
	val playerDoesNotExist by this.miniMessageDeserializer
	val amountIsNan by this.miniMessageDeserializer
	val lessThanMinimumAmount by this.miniMessageDeserializer
	val receiveError by this.miniMessageDeserializer
	val refundError by this.miniMessageDeserializer
	val sendSuccess: String by this
	val receiveSuccess: String by this
	val sendError by this.miniMessageDeserializer
	val notEnoughMoney by this.miniMessageDeserializer
	val notMuchPages by this.miniMessageDeserializer
	val header: String by this
	val row: String by this
	val footer: String by this
}