package fr.pickaria.vue.market

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.menu.open
import org.bukkit.entity.Player

@CommandAlias("market")
@CommandPermission("pickaria.command.market")
@Description("Ouvre le marché.")
class MarketCommand : BaseCommand() {
	@Default
	fun onDefault(player: Player) {
		player open "market"
	}
}
