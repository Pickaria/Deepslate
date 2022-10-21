package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.text.Component


internal object Config : ConfigProvider() {
	val currencyNameSingular: String by config
	val currencyNamePlural: String by config
	val balanceMessage: String by config
	val cantSendToYourself: Component by config
	val playerDoesNotExist: Component by config
	val amountIsNan: Component by config
	val lessThanMinimumAmount: Component by config
	val receiveError: Component by config
	val refundError: Component by config
	val sendSuccess: String by config
	val receiveSuccess: String by config
	val sendError: Component by config
	val notEnoughMoney: Component by config
	val notMuchPages: Component by config
	val header: String by config
	val row: String by config
	val footer: String by config
}