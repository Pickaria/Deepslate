package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.job.jobCount
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobModel
import fr.pickaria.model.job.JobType
import fr.pickaria.model.teleport.*
import fr.pickaria.model.teleport.Homes.homeName
import fr.pickaria.model.teleport.Homes.locationX
import fr.pickaria.model.teleport.Homes.locationY
import fr.pickaria.model.teleport.Homes.locationZ
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.h2.value.ValueVarchar
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.locks.Condition
import kotlin.math.log2


@CommandAlias("home")
@CommandPermission("pickaria.command.home")
class HomeTeleport(private val plugin: JavaPlugin) : BaseCommand() {

    companion object {

        fun homeFind(player: Player, name: String) = transaction {
            Home.find {
                (Homes.playerUuid eq player.uniqueId) and (Homes.homeName eq name)

            }.firstOrNull()

        }

        fun setupContext(manager: PaperCommandManager) {
            manager.commandContexts.registerContext(Home::class.java) {
                val arg: String = it.popFirstArg()

                homeFind(it.player, arg)
            }

            manager.commandCompletions.registerCompletion("ownhome") { context ->
                transaction {
                    Home.find { (Homes.playerUuid eq context.player.uniqueId) and (Homes.homeName like "${context.input}%") }
                        .map { it.homeName }
                }
            }

        }

        private val TAG = "HAS_TP_ONGOING"

    }

    @Default
    @CommandCompletion("@ownhome")
    @Description("Vous téléporte aléatoirement autour du spawn.")
    fun onDefault(player: Player, home: Home) {

        val now = Clock.System.now().plus(teleportConfig.delayBetweenTeleports, DateTimeUnit.SECOND)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val tpTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val history = transaction {
            History.find {
                Histories.playerUuid eq player.uniqueId
            }.firstOrNull()
        }

        val cost = 10.0

        val containsTaskTag = player.scoreboardTags.contains(TAG)

        val canTeleport = history?.let {
            it.lastTeleport < tpTime
        } ?: true

        if (homeFind(player, name) == null) {
            throw ConditionFailedException("Cette résidence n'existe pas.")
        }
        if (!containsTaskTag) {
            if (canTeleport) {
                if (player.has(Credit, cost)) {
                    player.sendMessage(teleportConfig.messageBeforeTeleport)
                    MiniMessage("<gray>La téléportation vous a couté <gold><amount><gray>.") {
                        "amount" to Credit.economy.format(cost)
                    }.send(player)
                    player.addScoreboardTag(TAG)

                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        player.withdraw(Credit, cost)
                        var homeLocation = getHomeLocation(player, name)
                        player.teleport(homeLocation)
                        val remove = player.scoreboardTags.remove(TAG)
                    }, 120L)
                    transaction {
                        history?.let {
                            it.lastTeleport = now
                        } ?: run {
                            History.new {
                                playerUuid = player.uniqueId
                                lastTeleport = now
                            }
                        }
                    }
                } else {
                    player.sendMessage(economyConfig.notEnoughMoney)
                }
            } else {
                throw ConditionFailedException("Patientez avant de vous téléporter de nouveau.")

            }
        } else {
            throw ConditionFailedException("Une téléportation est déjà en cours")
        }
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