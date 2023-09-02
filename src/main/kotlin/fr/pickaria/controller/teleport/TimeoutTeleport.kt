package fr.pickaria.controller.teleport

import fr.pickaria.controller.economy.withdraw
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.teleport.teleportConfig
import fr.pickaria.shared.MiniMessage
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

fun Player.cancelTeleport(plugin: JavaPlugin): Boolean {
    return getTimeoutTeleport()?.let {
        it.cancel()
        removeMetadata(KEY, plugin)
        true
    } ?: false
}

fun Player.teleportToLocationAfterTimeout(
    plugin: JavaPlugin,
    location: Location,
    cost: Double = 0.0,
    after: Long = 80
) {
    MiniMessage(teleportConfig.messageBeforeTeleport) {
        "time" to after / 20
    }.send(this)

    if (cost > 0) {
        MiniMessage("<gray>La téléportation va vous couter <gold><amount><gray> une fois téléporté.") {
            "amount" to Credit.economy.format(cost)
        }.send(this)
    }

    val task = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
        teleport(location)
        removeMetadata(KEY, plugin)
        setLastTeleportation(plugin)

        if (cost > 0) {
            withdraw(Credit, cost)
        }
    }, after)
    val timeoutTeleport = TimeoutTeleport(task, plugin, this)
    setMetadata(KEY, TimeoutTeleportMetadataValue(plugin, timeoutTeleport))
}

fun Player.hasOnGoingTeleport() = hasMetadata(KEY)
