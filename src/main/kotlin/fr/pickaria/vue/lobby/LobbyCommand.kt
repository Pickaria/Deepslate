package fr.pickaria.vue.lobby

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Default
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.mainConfig
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("lobby")
class LobbyCommand(private val plugin: JavaPlugin) : BaseCommand() {
    @Default
    @Conditions("can_teleport")
    fun onDefault(player: Player) {
        mainConfig.lobbyWorld?.let {
            player.teleportToLocationAfterTimeout(plugin, it.spawnLocation)
        } ?: player.sendMessage(Component.text("Le lobby est inaccessible"))
    }
}