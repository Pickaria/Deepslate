package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.text.Component


internal object Config : ConfigProvider() {
	val currencyNameSingular: String by config
	val currencyNamePlural: String by config
	val balanceMessage: String by config
	val cantSendToYourself: String by config
	val playerDoesNotExist: String by config
	val amountIsNan: String by config
	val lessThanMinimumAmount: String by config
	val receiveError: String by config
	val refundError: String by config
	val sendSuccess: String by config
	val receiveSuccess: String by config
	val sendError: String by config
	val notEnoughMoney: String by config
	val notMuchPages: String by config
	val header: String by config
	val row: String by config
	val footer: String by config
}