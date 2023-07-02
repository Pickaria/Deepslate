package fr.pickaria.vue.lobby

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import fr.pickaria.model.mainConfig
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

@CommandAlias("lobby")
class LobbyCommand : BaseCommand() {
	@Default
	fun onDefault(player: Player) {
		mainConfig.lobbyWorld?.let {
			player.teleport(it.spawnLocation)
		} ?: player.sendMessage(Component.text("Le lobby est inaccessible"))
	}
}