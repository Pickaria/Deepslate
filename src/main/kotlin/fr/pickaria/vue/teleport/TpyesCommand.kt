package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Optional
import co.aikar.commands.bukkit.contexts.OnlinePlayer
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
import org.bukkit.metadata.MetadataValue
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

@CommandAlias("tpyes|tpy|tpaccept")
@CommandPermission("pickaria.command.TpyesCommand")
class TpyesCommand(private val plugin: JavaPlugin) : BaseCommand() {

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
    @Description("accepte une demande de téléportation")
    fun onDefault(sender: Player) {


        val recipient = returnMetaData(sender)


        val now = Clock.System.now()
            .plus(teleportConfig.delayBetweenTeleports, DateTimeUnit.SECOND)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val tpTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val history = transaction {
            History.find {
                Histories.playerUuid eq recipient.uniqueId
            }.firstOrNull()
        }


        val containsTaskTag = recipient.scoreboardTags.contains(TAG)

        val canTeleport = history?.let {
//            println(now)
//            println(tpTime)
//            println(it.lastTeleport )
            it.lastTeleport < tpTime
        } ?: true

        if (sender is Player) {
            val recipient: Player = returnMetaData(sender)
            if (sender.hasMetadata(sender.name)) {
                sender.removeMetadata(sender.name,plugin)
                print("TP REQUEST IS TRUE")
                if (canTeleport) {
                    MiniMessage("<gold><player> à accepté votre demande de téléportation<gold>"){"player" to sender.name}.send(recipient)
                    recipient.sendMessage(teleportConfig.messageBeforeTeleport)
                    MiniMessage("<gray>Téléportation acceptée<gray>.").send(sender)

                    recipient.addScoreboardTag(TAG)

                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        recipient.teleport(sender)
                        println("tp")
                        println(recipient.scoreboardTags)
                        val remove = recipient.scoreboardTags.remove(TAG)
                        println(remove)
                    }, 120L)
                    transaction {
                        history?.let {
                            it.lastTeleport = now
                        } ?: run {
                            History.new {
                                playerUuid = recipient.uniqueId
                                lastTeleport = now
                            }
                        }
                    }
                } else {
                    // println(player.scoreboardTags)
                    //val remove = player.scoreboardTags.remove(TAG)
                    // println(remove)
                    throw ConditionFailedException("Erreur")
                }
            } else {
                throw ConditionFailedException("TP REQUEST IS FALSE")
            }
        }else{
            throw ConditionFailedException("Aucune demande de téléportation en cours")
        }
    }
    fun returnMetaData(sender: Player): Player {
        try {
            val metadataList: List<MetadataValue>? = sender.getMetadata(sender.name)
            val requestSenderValue: MetadataValue = metadataList!![0]
            val requestSender: String = requestSenderValue.asString()
            val recipient: Player = Bukkit.getPlayerExact(requestSender)!!
            return recipient
        } catch (e: Exception){
            throw ConditionFailedException("Aucune demande de téléportation n'est en cours")
        }

    }
    }


