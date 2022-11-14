package fr.pickaria.lock

import org.bukkit.block.Lockable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class Lock(): Listener {
    /**
     * Protection plugin for all containers.
     */
    // header of the sign, indicates that the container is locked
    val lockWord = "§6§1[Protected]"

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        /**
         * When a player tries to interact with a container, this function is called.
         */
        val player = event.player

        // debug
        player.sendMessage("Passed... onPlayerInteract")

        val block = event.clickedBlock
        if (block !is Lockable) return // lockable container?
        if (player.isSneaking && event.action == Action.LEFT_CLICK_BLOCK)
            locker(block, player) // locking/ unlocking the container
    }

    private fun locker(block: Lockable, player: Player) {
        /**
         * Toggles locking or not the container 'block', and sets the owner 'player' if locking.
         */
        when (block.isLocked) {
            true    -> unlock(block, player)
            false   -> lock(block, player)
        }
    }

    private fun lock(block: Lockable, player: Player) {}
    private fun unlock(block: Lockable, player: Player) {}

}
