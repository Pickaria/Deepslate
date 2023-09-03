package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.teleport.getHomeNames
import fr.pickaria.controller.teleport.homeCount
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.mainConfig
import fr.pickaria.model.teleport.Home
import fr.pickaria.model.teleport.Homes
import fr.pickaria.model.teleport.teleportConfig
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

    @CommandCompletion("@ownhome")
    @Description("Vous téléporte à la résidence choisie ou la résidence par défaut.")
    @Conditions("can_teleport")
    @Subcommand("teleport|tp")
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
        } ?: throw ConditionFailedException("Aucune résidence avec ce nom trouvée.")
    }

    @Subcommand("set")
    @CommandAlias("sethome")
    @Description("Créer une résidence à votre position actuelle.")
    fun onCreate(player: Player, @Default("home") name: String) {
        if (player.world == mainConfig.lobbyWorld) {
            throw ConditionFailedException("Cette commande ne peut pas être exécutée ici.")
        }

        if (player.homeCount() >= teleportConfig.homeLimit) { // TODO: Use permission for home limit
            throw ConditionFailedException("Vous possédez trop de résidences.")
        }

        homeFind(player, name)?.let {
            throw ConditionFailedException("Une résidence avec ce nom existe déjà, si vous souhaitez la modifier, supprimez-la avant de la recréer.")
        }

        val location = player.location

        try {
            transaction {
                Home.new {
                    playerUuid = player.uniqueId
                    homeName = name
                    world = player.world.uid
                    locationX = location.blockX
                    locationY = location.blockY
                    locationZ = location.blockZ
                }
            }
        } catch (_: Exception) {
            throw ConditionFailedException("Une erreur est survenue lors de l'enregistrement de la résidence. Essayez avec un autre nom.")
        }

        player.sendMessage(teleportConfig.homeRegistrationConfirm)
    }

    @Subcommand("delete")
    @CommandAlias("delhome")
    @Description("Supprime une résidence.")
    @CommandCompletion("@ownhome")
    fun onDelete(sender: Player, home: Home?) {
        home?.let {
            transaction { it.delete() }
            sender.sendMessage(teleportConfig.homeDeletionConfirm)
        } ?: throw ConditionFailedException("Aucune résidence avec ce nom trouvée.")
    }

    @HelpCommand
    @Default
    fun doHelp(help: CommandHelp) {
        help.showHelp()
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