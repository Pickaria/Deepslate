package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import createMetaDataTpTag
import fr.pickaria.model.teleport.Histories
import fr.pickaria.model.teleport.History
import fr.pickaria.model.teleport.teleportConfig
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import returnMetaDataTpa
import java.util.*

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

        val TargetTpa = "targetTpa" //recipient
        val SenderTpa = "senderTpa" //sender

        val recipient = returnMetaDataTpa(plugin,TargetTpa,sender)


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

        val canTeleport = history?.let {
//            println(now)
//            println(tpTime)
//            println(it.lastTeleport )
            it.lastTeleport < tpTime
        } ?: true

        if (sender is Player) {
            val recipient: Player = returnMetaDataTpa(plugin,TargetTpa,sender)
            if (sender.hasMetadata(TargetTpa)) {
                sender.removeMetadata(TargetTpa,plugin)
                recipient.removeMetadata(SenderTpa,plugin)
                print("TP REQUEST IS TRUE")
                if (canTeleport) {
                    MiniMessage("<gold><player> à accepté votre demande de téléportation<gold>"){"player" to sender.name}.send(recipient)
                    recipient.sendMessage(teleportConfig.messageBeforeTeleport)
                    MiniMessage("<gray>Téléportation acceptée<gray>.").send(sender)
                    createMetaDataTpTag(plugin,sender)

                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        recipient.teleport(sender)
                        recipient.removeMetadata(TAG,plugin)
                        println("tp")
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
                    throw ConditionFailedException("Erreur")
                }
            } else {
                throw ConditionFailedException("Aucune demande de téléportation en cours")
            }
        }else{
            throw ConditionFailedException("Erreur")
        }
    }
    }


