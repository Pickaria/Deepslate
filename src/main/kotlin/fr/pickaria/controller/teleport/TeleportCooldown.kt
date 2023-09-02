package fr.pickaria.controller.teleport

import fr.pickaria.model.now
import kotlinx.datetime.*
import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.java.JavaPlugin

class TeleportCooldown(val lastTeleportation: LocalDateTime)

class TeleportCooldownMetadataValue(private val plugin: JavaPlugin, private val teleportCooldown: TeleportCooldown) :
    MetadataValue {
    override fun value() = teleportCooldown

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

fun Player.getTeleportCooldown(): TeleportCooldown? {
    for (metadata in getMetadata(KEY)) {
        if (metadata is TeleportCooldownMetadataValue) {
            return metadata.value()
        }
    }

    return null
}

fun Player.canTeleport(delayBetweenTeleports: Int): Boolean = getTeleportCooldown()?.let {
    val nextTeleportationTime = Clock.System.now().minus(delayBetweenTeleports, DateTimeUnit.SECOND)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    it.lastTeleportation < nextTeleportationTime
} ?: true

fun Player.setLastTeleportation(plugin: JavaPlugin, lastTeleportation: LocalDateTime = now()) {
    val teleportCooldown = TeleportCooldown(lastTeleportation)
    setMetadata(KEY, TeleportCooldownMetadataValue(plugin, teleportCooldown))
}
