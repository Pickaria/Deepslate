package fr.pickaria.vue.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import fr.pickaria.controller.economy.balance
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.shared.MiniMessage
import org.bukkit.entity.Player

@CommandAlias("money|bal|balance")
@CommandPermission("pickaria.command.balance")
class MoneyCommand : BaseCommand() {
	@Default
	fun onDefault(player: Player) {
		MiniMessage(economyConfig.balanceMessage) {
			"balance" to Credit.economy.format(player.balance(Credit))
		}.send(player)
	}
}