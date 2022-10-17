package fr.pickaria.teleport

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


class TeleportController(private val plugin: JavaPlugin) {
	val TELEPORTCOOLDOWN = 100L
	val map = mutableMapOf<Player, Pair<Player, Boolean>>()

	fun cooldownTeleport(player: Player, location: Location) {
		val message = miniMessage.deserialize(
			teleportConfig.teleportCooldownMessage,
			Placeholder.unparsed("cooldown", (TELEPORTCOOLDOWN / 20).toString())
		)

		player.sendMessage(message)

		location.add(0.5, 0.0, 0.5)

		object : BukkitRunnable() {
			override fun run() {
				player.teleport(location)
				val message = miniMessage.deserialize(teleportConfig.teleportSummonedMessage)
				player.sendMessage(message)
			}
		}.runTaskLater(plugin, TELEPORTCOOLDOWN)
	}

	/**
	 * Create a teleport request to teleport Sender to Recipient
	 */
	fun createTpRequest(sender: Player, recipient: Player, direction: Boolean = true): Boolean {
		return if (recipient == sender) {
			sender.sendMessage(miniMessage.deserialize(teleportConfig.teleportSelfError))
			false
		} else {
			map[sender] = recipient to direction
			true
		}
	}

	fun sendTpRequestMessage(sender: Player, message: net.kyori.adventure.text.Component) {
		sender.sendMessage(message)
			val messageCommand = miniMessage.deserialize(teleportConfig.teleportCommandMessage)
					sender.sendMessage(messageCommand)

		}
	}