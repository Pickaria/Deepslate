package fr.pickaria.vue.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import fr.pickaria.controller.economy.sendTo
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.SendResponse
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.shared.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("pay")
@CommandPermission("pickaria.command.pay")
class PayCommand : BaseCommand() {
	@Default
	@Syntax("<recipient> <amount>")
	@CommandCompletion("@players")
	fun onDefault(player: CommandSender, onlinePlayer: OnlinePlayer, amount: Double) {
		if (amount <= 0.01) {
			throw InvalidCommandArgument(economyConfig.lessThanMinimumAmount)
		}

		val recipient = onlinePlayer.player as Player
		val sender = player as Player

		if (recipient === sender) {
			throw InvalidCommandArgument(economyConfig.cantSendToYourself)
		}

		when (sendTo(Credit, sender, recipient, amount)) {
			SendResponse.RECEIVE_ERROR -> sender.sendMessage(economyConfig.receiveError)
			SendResponse.REFUND_ERROR -> sender.sendMessage(economyConfig.refundError)
			SendResponse.SUCCESS -> {
				val format = Credit.economy.format(amount)

				MiniMessage(economyConfig.receiveSuccess) {
					"sender" to sender.displayName()
					"amount" to format
				}.send(recipient)

				MiniMessage(economyConfig.sendSuccess) {
					"amount" to format
				}.send(sender)
			}

			SendResponse.SEND_ERROR -> sender.sendMessage(economyConfig.sendError)
			SendResponse.NOT_ENOUGH_MONEY -> sender.sendMessage(economyConfig.notEnoughMoney)
		}
	}
}