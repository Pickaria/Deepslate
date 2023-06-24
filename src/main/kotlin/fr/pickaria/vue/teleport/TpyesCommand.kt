package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.controller.teleport.clearTeleportRequest
import fr.pickaria.controller.teleport.getTeleportRequest
import fr.pickaria.controller.teleport.hasOnGoingTeleport
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

@CommandAlias("tpyes|tpy|tpaccept")
@CommandPermission("pickaria.command.TpyesCommand")
class TpyesCommand(private val plugin: JavaPlugin) : BaseCommand() {
	@Default
	@Description("Accepte une demande de téléportation")
	fun onDefault(sender: Player) {
		if (sender.hasOnGoingTeleport()) {
			throw ConditionFailedException("Une téléportation est déjà en cours.")
		}

		sender.getTeleportRequest()?.let {
			// Make sure it is the recipient who executes the command
			if (sender == it.sender) {
				throw ConditionFailedException("Vous ne pouvez pas accepter une demande que vous avez envoyé.")
			}

			it.sender.teleportToLocationAfterTimeout(plugin, it.recipient.location)

			MiniMessage("<gold><player><gray> à accepté votre demande de téléportation.") {
				"player" to it.recipient.displayName()
			}.send(it.sender)

			MiniMessage("<gray>Téléportation acceptée.").send(it.recipient)

			sender.clearTeleportRequest(plugin)
		} ?: throw ConditionFailedException("Aucune demande de téléportation en cours.")
	}
}
