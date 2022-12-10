package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider


internal object Config : ConfigProvider() {
	val currencies by this.sectionLoader<Currency>()

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