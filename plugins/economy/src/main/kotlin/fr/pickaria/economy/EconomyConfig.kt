package fr.pickaria.economy

import org.bukkit.configuration.file.FileConfiguration

internal class EconomyConfig(config: FileConfiguration) {
	val currencyNameSingular = config.getString("currency_name_singular")!!
	val currencyNamePlural = config.getString("currency_name_plural")!!

	val cantSendToYourself = miniMessage.deserialize(config.getString("cant_send_to_yourself")!!)
	val playerDoesNotExist = miniMessage.deserialize(config.getString("player_does_not_exist")!!)
	val amountIsNan = miniMessage.deserialize(config.getString("amount_is_nan")!!)
	val lessThanMinimumAmount = miniMessage.deserialize(config.getString("less_than_minimum_amount")!!)
	val receiveError = miniMessage.deserialize(config.getString("receive_error")!!)
	val refundError = miniMessage.deserialize(config.getString("refund_error")!!)
	val sendError = miniMessage.deserialize(config.getString("send_error")!!)
	val notEnoughMoney = miniMessage.deserialize(config.getString("not_enough_money")!!)

	val balanceMessage = config.getString("balance_message")!!
	val sendSuccess = config.getString("send_success")!!
	val receiveSuccess = config.getString("receive_success")!!

	val notMuchPages = miniMessage.deserialize(config.getString("not_much_pages")!!)
	val header = config.getString("header")!!
	val row = config.getString("row")!!
	val footer = config.getString("footer")!!
}