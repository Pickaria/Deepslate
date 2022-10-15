package fr.pickaria.teleport

import org.bukkit.configuration.Configuration

internal class TeleportConfig(private val config: Configuration) {
	val teleportCooldownMessage = config.getString("teleport_cooldown_message")!!
}