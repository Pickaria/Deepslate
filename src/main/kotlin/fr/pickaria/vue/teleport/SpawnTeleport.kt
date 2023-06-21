package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Optional
import fr.pickaria.controller.economy.balance
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.teleport.Histories
import fr.pickaria.model.teleport.History
import fr.pickaria.model.teleport.TeleportConfiguration
import fr.pickaria.model.teleport.teleportConfig
import fr.pickaria.shared.MiniMessage
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.h2.util.Task
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Handler
import kotlin.concurrent.schedule
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.sin
import kotlin.random.Random

@CommandAlias("spawn")
@CommandPermission("pickaria.command.spawnteleport")
class SpawnTeleport(private val plugin: JavaPlugin) : BaseCommand() {

    companion object {

        private val TAG = "HAS_TP_ONGOING"

    }

    // https://github.com/aikar/commands/wiki/Using-ACF
    // https://github.com/aikar/commands/wiki/Locales
    @Default
    @Description("Vous téléporte au spawn.")
    fun onDefault(player: Player) {

        val cost = log2(500.0) * teleportConfig.rtpMultiplier

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

        val containsTaskTag = player.scoreboardTags.contains(TAG)

        val canTeleport = history?.let {
//            println(now)
//            println(tpTime)
//            println(it.lastTeleport )
            it.lastTeleport < tpTime
        } ?: true

        if (!containsTaskTag) {
//            println("cantp")
            if (canTeleport) {
                if (player.has(Credit, cost)) {
                    player.sendMessage(teleportConfig.messageBeforeTeleport)
                    MiniMessage("<gray>La téléportation vous a couté <gold><amount><gray>.") {
                        "amount" to Credit.economy.format(cost)
                    }.send(player)
                    player.addScoreboardTag(TAG)

                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        player.withdraw(Credit, cost)
                        player.teleport(player.world.spawnLocation)
                        // println("tp")
//                        println(player.scoreboardTags)
                        val remove = player.scoreboardTags.remove(TAG)
//                        println(remove)
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
//                println(player.scoreboardTags)
//                val remove = player.scoreboardTags.remove(TAG)
//                println(remove)
                throw ConditionFailedException("Patientez avant de vous téléporter de nouveau.")

            }
        } else {
            throw ConditionFailedException("Une téléportation est déjà en cours")
        }
    }

}


