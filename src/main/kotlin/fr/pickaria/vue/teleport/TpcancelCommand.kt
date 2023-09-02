package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.controller.teleport.cancelTeleport
import fr.pickaria.controller.teleport.clearTeleportRequest
import fr.pickaria.controller.teleport.getTeleportRequest
import fr.pickaria.controller.teleport.hasOnGoingTeleport
import fr.pickaria.shared.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("tpcancel")
@CommandPermission("pickaria.command.TpcancelCommand")
class TpcancelCommand(private val plugin: JavaPlugin) : BaseCommand() {
    @Default
    @Description("Annule une demande de téléportation")
    fun onDefault(sender: Player) {
        if (sender.hasOnGoingTeleport()) {
            sender.cancelTeleport(plugin)
            MiniMessage("<gray>Téléportation annulée.").send(sender)
        } else {

            sender.getTeleportRequest()?.let {
                if (sender == it.recipient) {
                    throw ConditionFailedException("Vous ne pouvez pas annuler une demande reçue, refusez-la plutôt.")
                }

                MiniMessage("<red><player> a annulé la demande de téléportation.") {
                    "player" to sender.displayName()
                }.send(it.recipient)

                MiniMessage("<gray>Téléportation annulée.").send(it.sender)

                sender.clearTeleportRequest(plugin)
            } ?: throw ConditionFailedException("Aucune demande de téléportation à annuler.")
        }
    }
}
