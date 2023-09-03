package fr.pickaria.controller.teleport

import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.java.JavaPlugin

class TeleportRequest(val sender: Player, val recipient: Player)

class TeleportRequestMetadataValue(private val plugin: JavaPlugin, private val teleportStatus: TeleportRequest) :
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

private const val KEY = "pickaria:teleport_request"

fun Player.getTeleportRequest(): TeleportRequest? {
	for (metadata in getMetadata(KEY)) {
		if (metadata is TeleportRequestMetadataValue) {
			return metadata.value()
		}
	}

	return null
}

fun Player.sendTeleportRequestTo(plugin: JavaPlugin, player: Player): TeleportRequest {
	val teleportRequest = TeleportRequest(this, player)
	this.setMetadata(KEY, TeleportRequestMetadataValue(plugin, teleportRequest))
	player.setMetadata(KEY, TeleportRequestMetadataValue(plugin, teleportRequest))

	return teleportRequest
}

fun Player.clearTeleportRequest(plugin: JavaPlugin) {
	getTeleportRequest()?.let {
		it.sender.removeMetadata(KEY, plugin)
		it.recipient.removeMetadata(KEY, plugin)
	}
}

fun Player.hasTeleportRequest() = hasMetadata(KEY)