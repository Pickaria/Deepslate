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
import java.lang.Exception
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

@CommandAlias("tpcancel")
@CommandPermission("pickaria.command.TpcancelCommand")
class TpcancelCommand(private val plugin: JavaPlugin) : BaseCommand() {

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
    @Description("annule une demande de téléportation")
    fun onDefault(sender: Player) {

        if (sender is Player) {
                    val recipient: Player = returnMetaData(sender)

                    // Utilisez les variables requestSender et recipient comme nécessaire

                    MiniMessage("<gray><player> à annulé votre demande de téléportation<gray>"){"player" to sender.name}.send(recipient)
                    MiniMessage("<gray>demande de téléportation annulée<gray>.").send(sender)
                    sender.removeScoreboardTag(TAG)
                    sender.removeMetadata(sender.name,plugin)
                    recipient.removeMetadata(recipient.name,plugin)

            } else {
                throw ConditionFailedException("Erreur")
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


