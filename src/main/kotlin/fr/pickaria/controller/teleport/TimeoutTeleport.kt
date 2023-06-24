package fr.pickaria.controller.teleport

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class TimeoutTeleport(private val task: BukkitTask, private val plugin: JavaPlugin, private val player: Player) {
	fun cancel() {
		task.cancel()
		player.removeMetadata(KEY, plugin)
	}
}

class TimeoutTeleportMetadataValue(private val plugin: JavaPlugin, private val teleportStatus: TimeoutTeleport) :
	MetadataValue {
	override fun value() = teleportStatus

	override fun asInt(): Int {
		TODO("Not yet implemented")
	}

	override fun asFloat(): Float {
		TODO("Not yet implemented")
	}

	override fun asDouble(): Double {
		TODO("Not yet implemented")
	}

	override fun asLong(): Long {
		TODO("Not yet implemented")
	}

	override fun asShort(): Short {
		TODO("Not yet implemented")
	}

	override fun asByte(): Byte {
		TODO("Not yet implemented")
	}

	override fun asBoolean(): Boolean {
		TODO("Not yet implemented")
	}

	override fun asString(): String {
		TODO("Not yet implemented")
	}

	override fun getOwningPlugin() = plugin

	override fun invalidate() {
		TODO("Not yet implemented")
	}
}

private const val KEY = "pickaria:timeout_teleport"

private fun Player.getTimeoutTeleport(): TimeoutTeleport? {
	for (metadata in getMetadata(KEY)) {
		if (metadata is TimeoutTeleportMetadataValue) {
			return metadata.value()
		}
	}

	return null
}

fun Player.cancelTeleport(plugin: JavaPlugin) {
	getTimeoutTeleport()?.let {
		it.cancel()
		removeMetadata(KEY, plugin)
	}
}

fun Player.teleportToLocationAfterTimeout(plugin: JavaPlugin, location: Location, after: Long = 120) {
	val task = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
		teleport(location)
		removeMetadata(KEY, plugin)
	}, after)
	val timeoutTeleport = TimeoutTeleport(task, plugin, this)
	setMetadata(KEY, TimeoutTeleportMetadataValue(plugin, timeoutTeleport))
}

fun Player.hasOnGoingTeleport() = hasMetadata(KEY)
