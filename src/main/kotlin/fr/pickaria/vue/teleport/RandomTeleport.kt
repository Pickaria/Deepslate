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

@CommandAlias("rand&omteleport|rtp|tpr")
@CommandPermission("pickaria.command.randomteleport")
class RandomTeleport(private val plugin: JavaPlugin) : BaseCommand() {

    companion object {

         private val TAG = "HAS_TP_ONGOING"


        private val EXCLUDED_BLOCKS = setOf(
            // Oceans
            Biome.OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,

            // Rivers
            Biome.RIVER,
            Biome.FROZEN_RIVER,

            // Powder snow biomes
//			Biome.SNOWY_SLOPES,
//			Biome.GROVE,
        )

        private val EXCLUDED_MATERIALS = setOf(
            Material.MAGMA_BLOCK,
            Material.CACTUS,
            Material.POWDER_SNOW,
        )
    }

    // https://github.com/aikar/commands/wiki/Using-ACF
    // https://github.com/aikar/commands/wiki/Locales
    @Default
    @Description("Vous téléporte aléatoirement autour du spawn.")
    fun onDefault(player: Player, @Default("1000") maxRadius: UInt) {
        val location = getRandomLocation(player, maxRadius.toInt())
        val cost = log2(maxRadius.toDouble()) * teleportConfig.rtpMultiplier

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
            if(canTeleport) {
                if (player.has(Credit, cost)) {
                    player.sendMessage(teleportConfig.messageBeforeTeleport)
                    MiniMessage("<gray>La téléportation vous a couté <gold><amount><gray>.") {
                        "amount" to Credit.economy.format(cost)
                    }.send(player)
                    player.addScoreboardTag(TAG)

                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        player.withdraw(Credit, cost)
                        player.teleport(location)
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
            }else{
//                println(player.scoreboardTags)
//                val remove = player.scoreboardTags.remove(TAG)
//                println(remove)
                throw ConditionFailedException("Patientez avant de vous téléporter de nouveau.")

            }
        } else {
            throw ConditionFailedException("Une téléportation est déjà en cours")
        }
    }

    private fun getRandomLocation(sender: Player, maxRadius: Int): Location {
        var tries = 0
        var x: Double
        var z: Double
        var location: Location

        do {
            val random = Random.nextDouble() * 2 - 1
            x = cos(random) * maxRadius
            z = sin(random) * maxRadius
            location = Location(sender.world, x, 0.0, z)
            location.y = sender.world.getHighestBlockYAt(location).toDouble()
        } while (tries++ < 10 && (EXCLUDED_BLOCKS.contains(sender.world.getBiome(location)) || !location.block.type.isSolid || EXCLUDED_MATERIALS.contains(location.block.type)))

        location.y += 1.0

        return location
    }
}

