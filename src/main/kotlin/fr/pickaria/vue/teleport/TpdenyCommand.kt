package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.model.teleport.teleportConfig
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import returnMetaDataTpa
import java.util.*

@CommandAlias("tpno|tpdeny")
@CommandPermission("pickaria.command.TpdenyCommand")
class TpdenyCommand(private val plugin: JavaPlugin) : BaseCommand() {

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
    @Description("refuse une demande de téléportation")
    fun onDefault(sender: Player) {

        val TargetTpa = "targetTpa"
        val SenderTpa = "senderTpa"

        val now = Clock.System.now()
            .plus(teleportConfig.delayBetweenTeleports, DateTimeUnit.SECOND)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val tpTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        if (sender is Player) {
           val recipient: Player = returnMetaDataTpa(plugin,TargetTpa,sender)
            if (sender.hasMetadata(TargetTpa)) {
                print("TP REQUEST IS TRUE")
                recipient.removeMetadata(SenderTpa,plugin)
                sender.removeMetadata(TargetTpa,plugin)
                    MiniMessage("<red><player> à refusé votre demande de téléportation<red>"){"player" to sender.name}.send(recipient)
                    MiniMessage("<gray>Téléportation refusé<gray>.").send(sender)
            } else {
                throw ConditionFailedException("Aucune demande de téléportation en cours")
            }
        }else{
            throw ConditionFailedException("Erreur")
        }
    }
    }


