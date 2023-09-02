package fr.pickaria.vue.lobby

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Default
import fr.pickaria.controller.teleport.canTeleport
import fr.pickaria.controller.teleport.hasOnGoingTeleport
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.mainConfig
import fr.pickaria.model.teleport.teleportConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("lobby")
class LobbyCommand(private val plugin: JavaPlugin) : BaseCommand() {
    @Default
    @Conditions("can_teleport")
    fun onDefault(player: Player) {
        mainConfig.lobbyWorld?.let {
            if (player.hasOnGoingTeleport()) {
                throw ConditionFailedException("Une téléportation est déjà en cours")
            }

            if (!player.canTeleport(teleportConfig.delayBetweenTeleports)) {
                throw ConditionFailedException("Veuillez attendre avant votre prochaine téléportation")
            }

            player.sendMessage(Component.text("Téléportation dans 4 secondes.", NamedTextColor.GRAY))
            player.teleportToLocationAfterTimeout(plugin, it.spawnLocation)
        } ?: player.sendMessage(Component.text("Le lobby est inaccessible"))
    }
}