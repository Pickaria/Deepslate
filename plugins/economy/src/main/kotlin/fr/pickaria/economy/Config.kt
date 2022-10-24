package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.text.Component


internal object Config : ConfigProvider() {
	val currencyNameSingular: String by this
	val currencyNamePlural: String by this
	val balanceMessage: String by this
	val cantSendToYourself: Component by this.deserializer
	val playerDoesNotExist: Component by this.deserializer
	val amountIsNan: Component by this.deserializer
	val lessThanMinimumAmount: Component by this.deserializer
	val receiveError: Component by this.deserializer
	val refundError: Component by this.deserializer
	val sendSuccess: String by this
	val receiveSuccess: String by this
	val sendError: Component by this.deserializer
	val notEnoughMoney: Component by this.deserializer
	val notMuchPages: Component by this.deserializer
	val header: String by this
	val row: String by this
	val footer: String by this
}