package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import fr.pickaria.controller.teleport.hasOnGoingTeleport
import fr.pickaria.controller.teleport.hasTeleportRequest
import fr.pickaria.controller.teleport.sendTeleportRequestTo
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

@CommandAlias("tpa")
@CommandPermission("pickaria.command.TpaCommand")
class TpaCommand(private val plugin: JavaPlugin) : BaseCommand() {
	@Default
	@Description("Envoie une demande de teleportation à quelqu'un.")
	fun onDefault(sender: Player, onlinePlayer: OnlinePlayer) {
		if (sender.hasOnGoingTeleport()) {
			throw ConditionFailedException("Une téléportation est déjà en cours.")
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

		MiniMessage("<gray>Demande de téléportation envoyé.").send(teleportRequest.sender)
		MiniMessage("<gold><player><gray> demande à se téléporter à vous.") {
			"player" to teleportRequest.sender.displayName()
		}.send(teleportRequest.recipient)
	}
}
