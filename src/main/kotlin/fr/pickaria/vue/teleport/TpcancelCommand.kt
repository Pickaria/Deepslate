package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import returnMetaDataTpa
import java.util.*

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

        val TargetTpa = "targetTpa" //recipient
        val SenderTpa = "senderTpa" //sender

        if (sender is Player) {
            val recipient: Player = returnMetaDataTpa(plugin,SenderTpa,sender)
            if(sender.hasMetadata(SenderTpa))
                sender.removeMetadata(SenderTpa,plugin)
                recipient.removeMetadata(TargetTpa,plugin)

                    // Utilisez les variables requestSender et recipient comme nécessaire
                    MiniMessage("<gray><player> à annulé votre demande de téléportation<gray>"){"player" to sender.name}.send(recipient)
                    MiniMessage("<gray>demande de téléportation annulée<gray>.").send(sender)


            } else {
                throw ConditionFailedException("Erreur")
            }
        }
    }


