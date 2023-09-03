package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import fr.pickaria.controller.teleport.hasOnGoingTeleport
import fr.pickaria.controller.teleport.hasTeleportRequest
import fr.pickaria.controller.teleport.sendTeleportRequestTo
import fr.pickaria.shared.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("tpa")
@CommandPermission("pickaria.command.TpaCommand")
class TpaCommand(private val plugin: JavaPlugin) : BaseCommand() {
    @Default
    @Description("Envoie une demande de teleportation à quelqu'un.")
    @Conditions("can_teleport")
    fun onDefault(sender: Player, onlinePlayer: OnlinePlayer) {
        if (sender == onlinePlayer.player) {
            throw ConditionFailedException("Une demande a déjà été envoyée, annulez-la avant.")
        }

        if (sender.hasTeleportRequest()) {
            throw ConditionFailedException("Une demande a déjà été envoyée, annulez-la avant.")
        }

        val recipient = onlinePlayer.player

        if (sender == recipient) {
            throw ConditionFailedException("Vous ne pouvez pas vous téléporter sur vous même.")
        }

        if (recipient.hasOnGoingTeleport()) {
            throw ConditionFailedException("Le destinataire a déjà une téléportation en cours.")
        }

        val teleportRequest = sender.sendTeleportRequestTo(plugin, recipient)

        MiniMessage("<gray>Demande de téléportation envoyé.<newline><gold><click:run_command:/tpcancel>[Annuler]</click>").send(
            teleportRequest.sender
        )
        MiniMessage("<gold><player><gray> demande à se téléporter à vous.<newline><gold><click:run_command:/tpyes>[Accepter]</click> <click:run_command:/tpno>[Refuser]</click>") {
            "player" to teleportRequest.sender.displayName()
        }.send(teleportRequest.recipient)
    }
}
