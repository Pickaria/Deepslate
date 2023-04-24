package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.teleport.*
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
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.locks.Condition
import kotlin.math.log2


@CommandAlias("home")
@CommandPermission("pickaria.command.home")
class HomeTeleport(private val plugin: JavaPlugin) : BaseCommand() {

    companion object {

        private val TAG = "HAS_TP_ONGOING"

    }

    @Default
    @Description("Vous téléporte aléatoirement autour du spawn.")
    fun onDefault(player: Player, @Default("home")name: String) {

        val now = Clock.System.now()
            .plus(teleportConfig.delayBetweenTeleports, DateTimeUnit.SECOND)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val tpTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

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
                        var homeLocation = getHomeLocation(player)
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

    private fun getHomeLocation(sender: Player): Location {
        var newLocation: Location

        val home = transaction {
            Home.find {
                Homes.playerUuid eq sender.uniqueId
            }.firstOrNull()
        }

        var newLocationX = home?.let{
            it.locationX.toDouble()
        }
        var newLocationY = home?.let{
            it.locationY.toDouble()
        }
        var newLocationZ = home?.let{
            it.locationZ.toDouble()
        }

        newLocation = Location(sender.world, newLocationX!!, newLocationY!!, newLocationZ!!)
        return newLocation
    }
}