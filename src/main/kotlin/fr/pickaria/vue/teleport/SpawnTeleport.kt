package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.economy.Credit
import fr.pickaria.shared.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("spawn")
@CommandPermission("pickaria.command.spawnteleport")
class SpawnTeleport(private val plugin: JavaPlugin) : BaseCommand() {
    @Default
    @Description("Vous téléporte au spawn.")
    @Conditions("can_teleport")
    fun onDefault(player: Player) {
        val cost = 20.0 // TODO: Figure out a fair price

        if (player.has(Credit, cost)) {
            player.teleportToLocationAfterTimeout(plugin, player.world.spawnLocation, cost)
        } else {
            MiniMessage("Erreur: <red>Il faut <gold><amount><red> pour effectuer la téléportation.") {
                "amount" to Credit.economy.format(cost)
            }.send(player)
        }
    }
}


