package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.teleport.getHomeNames
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.teleport.Home
import fr.pickaria.model.teleport.Homes
import fr.pickaria.shared.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction


@CommandAlias("home")
@CommandPermission("pickaria.command.home")
class HomeTeleport(private val plugin: JavaPlugin, manager: PaperCommandManager) : BaseCommand() {
    private fun homeFind(player: Player, name: String) = transaction {
        Home.find {
            (Homes.playerUuid eq player.uniqueId) and (Homes.homeName eq name)
        }.firstOrNull()
    }

    init {
        manager.commandContexts.registerContext(Home::class.java) {
            val arg: String = it.popFirstArg()
            homeFind(it.player, arg)
        }

        manager.commandCompletions.registerCompletion("ownhome") { context ->
            context.player.getHomeNames(context.input)
        }
    }

    @Default
    @CommandCompletion("@ownhome")
    @Description("Vous téléporte à la résidence choisie ou la résidence par défaut.")
    @Conditions("can_teleport")
    fun onDefault(player: Player, @Optional home: Home?) {
        home?.let {
            val cost = 10.0 // TODO: Add to config

            if (player.has(Credit, cost)) {
                val homeLocation = getHomeLocation(player, name)
                player.teleportToLocationAfterTimeout(plugin, homeLocation, cost)
            } else {
                MiniMessage("Erreur: <red>Il faut <gold><amount><gray> pour effectuer la téléportation.") {
                    "amount" to Credit.economy.format(cost)
                }.send(player)
            }
        } ?: throw ConditionFailedException("Cette résidence n'existe pas.")
    }

    private fun getHomeLocation(player: Player, name: String): Location {
        val home = homeFind(player, name)

        val newLocationX = home?.locationX?.toDouble()
        val newLocationY = home?.locationY?.toDouble()
        val newLocationZ = home?.locationZ?.toDouble()
        val world = home?.world?.let { Bukkit.getWorld(it) }

        return Location(world, newLocationX!!, newLocationY!!, newLocationZ!!).add(0.5, 0.0, 0.5)
    }
}