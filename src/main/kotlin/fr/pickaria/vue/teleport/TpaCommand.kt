package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import createMetaDataTpTag
import createMetaDataTpa
import fr.pickaria.model.teleport.Histories
import fr.pickaria.model.teleport.History
import fr.pickaria.model.teleport.teleportConfig
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@CommandAlias("tpa")
@CommandPermission("pickaria.command.TpaCommand")
class TpaCommand(private val plugin: JavaPlugin) : BaseCommand()  {

    companion object {
        private val TAG = "HAS_TP_ONGOING"
    }



    // https://github.com/aikar/commands/wiki/Using-ACF
    // https://github.com/aikar/commands/wiki/Locales

    @Default
    @Description("Envoie une demande de teleportation à quelqu'un.")
    fun onDefault(sender: Player, onlinePlayer: OnlinePlayer) {

        val recipient = onlinePlayer.player as Player



        val now = Clock.System.now()
            .plus(teleportConfig.delayBetweenTeleports, DateTimeUnit.SECOND)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val metadataList: List<MetadataValue>? = recipient.getMetadata(recipient.name)

        val tpTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val history = transaction {
            History.find {
                Histories.playerUuid eq sender.uniqueId
            }.firstOrNull()
        }

        val canTeleport = history?.let {
//            println(now)
//            println(tpTime)
//            println(it.lastTeleport )
            it.lastTeleport < tpTime
        } ?: true

        if (recipient != sender) {
            if (!sender.hasMetadata(TAG)) {
                println("cantp")
                if (canTeleport) {
                    MiniMessage("<gray>Demande de téléportation envoyé<gray>.").send(sender)
                    MiniMessage("<gold><player> demande à se téléporter à vous<gold>"){ "player" to sender.name}.send(recipient)
                    createMetaDataTpa(plugin,sender,recipient)
                    createMetaDataTpTag(plugin,sender)
                    println(recipient.name)
                    print(metadataList)
                } else {
                    throw ConditionFailedException("Patientez avant de vous téléporter de nouveau.")
                }
            } else {
                throw ConditionFailedException("Une téléportation est déjà en cours")
            }
        } else {
            throw ConditionFailedException("vous ne pouvez pas vous téléporter sur vous même")
        }
    }
}



