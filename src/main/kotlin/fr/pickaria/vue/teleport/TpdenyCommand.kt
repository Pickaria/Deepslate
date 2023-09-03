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
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

@CommandAlias("tpno|tpdeny")
@CommandPermission("pickaria.command.TpdenyCommand")
class TpdenyCommand(private val plugin: JavaPlugin) : BaseCommand() {
	@Default
	@Description("refuse une demande de téléportation")
	fun onDefault(sender: Player) {
		if (sender.hasOnGoingTeleport()) {
			throw ConditionFailedException("Une téléportation est déjà en cours.")
		}

		sender.getTeleportRequest()?.let {
			// Make sure it is the recipient who executes the command
			if (sender == it.sender) {
				throw ConditionFailedException("Vous ne pouvez pas refuser une demande envoyée, annulez-la plutôt.")
			}

			MiniMessage("<red><player> a refusé votre demande de téléportation.") {
				"player" to sender.displayName()
			}.send(it.sender)

			MiniMessage("<gray>Téléportation refusé.").send(it.recipient)

			sender.clearTeleportRequest(plugin)
		} ?: throw ConditionFailedException("Aucune demande de téléportation en cours.")
	}
}
