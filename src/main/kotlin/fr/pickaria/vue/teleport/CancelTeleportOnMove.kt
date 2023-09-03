package fr.pickaria.vue.teleport

import fr.pickaria.controller.teleport.cancelTeleport
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin

class CancelTeleportOnMove(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun cancelTeleportOnMove(event: PlayerMoveEvent) {
        if (event.hasChangedPosition()) {
            event.player.cancelTeleport(plugin)
        }
    }
}