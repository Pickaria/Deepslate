package fr.pickaria.teleport

import org.bukkit.configuration.Configuration

internal class TeleportConfig(private val config: Configuration) {
	val teleportCooldownMessage = config.getString("teleport_cooldown_message")!!
	val teleportCommandMessage = config.getString("teleport_command_message")!!
	val teleportYouMessage = config.getString("teleport_you_message")!!
	val teleportTheyMessage = config.getString("teleport_they_message")!!
	val teleportAlreadyError = config.getString("teleport_already_error")!!
	val teleportSentMessage = config.getString("teleport_sent_message")!!
	val teleportAcceptMessage = config.getString("teleport_accept_message")!!
	val teleportDenyMessage = config.getString("teleport_deny_message")!!
	val teleportNotpMessage = config.getString("teleport_notp_message")!!
	val teleportSummonedMessage = config.getString("teleport_summoned_message")!!
	val teleportSelfError = config.getString("teleport_self_error")!!
	val rtpNotenoughError = config.getString("rtp_notenough_error")!!
	val rtpError = config.getString("rtp_error")!!
	val homeCreateMessage = config.getString("home_create_message")!!
	val homeDeleteMessage = config.getString("home_delete_message")!!
	val homeNotexistMessage = config.getString("home_notexist_message")!!
	val homeNotsafeError = config.getString("home_notsafetp_error")!!
	val homeNotsafecreateError = config.getString("home_notsafecreate_error")!!
}
