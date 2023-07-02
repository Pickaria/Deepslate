package fr.pickaria.vue.lobby

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.mainConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("lobby")
class LobbyCommand(private val plugin: JavaPlugin) : BaseCommand() {
	@Default
	fun onDefault(player: Player) {
		mainConfig.lobbyWorld?.let {
			player.sendMessage(Component.text("Téléportation dans 6 secondes.", NamedTextColor.GRAY))
			player.teleportToLocationAfterTimeout(plugin, it.spawnLocation)
		} ?: player.sendMessage(Component.text("Le lobby est inaccessible"))
	}
}