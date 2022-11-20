package fr.pickaria.lock

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.block.Beacon
import org.bukkit.block.Block
import org.bukkit.block.Container
import org.bukkit.block.Lockable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * ###------- WORK IN PROGRESS
 *
 * 'Lock' is made for Lockable containers.
 *   - A locked container have an owner.
 *   - The owner can have trusted players on its container.
 *   - Only the owner can break the protection.
 *   - Trusted members can access the container, but not break the protection.
 */
class Lock(plugin: Main): Listener {
    private val namespace = NamespacedKey(plugin, "lock")

    /**
     * Called when a player tries to punch a block.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBlockDamage(event: BlockDamageEvent) {
        with(event) {
            if (!player.isSneaking) return
            when (block.state) {
                is Lockable -> {
                    val container = block.state as Lockable
                    player.sendMessage("key: ${container.lock}")
                    if (!container.isLocked) lock(container, player)
                }
            }
        }
    }

    /**
     * Called when a player tries to totally break a block.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        //TODO: control thieves and bypasses (one-click break)

        with(event) {
            if (player.gameMode == GameMode.CREATIVE) {
                // admin thing, properly unlocks
                unlock(block.state as Lockable)
                return
            }

            when (block.state) {
                is Lockable -> {
                    val container = (block.state as Lockable)
                    if (container.isLocked) {
                        if (!isOwner(container, player)) {
                            player.sendMessage("Ce bloc est verrouillé.")
                            isCancelled = true
                        }
                        else unlock(container)
                    }
                }
            }
            player.sendMessage("Successfully unlocked.")
        }
    }

    /**
     * Locks a container and sets a player as its owner.
     * @param container, the Container to lock
     * @param player, the owner
     */
    fun lock(container: Lockable, player: Player) {
        if (container is Beacon)
                (container as PersistentDataContainer).set(namespace, PersistentDataType.STRING, player.uniqueId.toString())
        else
                (container as Container).persistentDataContainer.set(namespace, PersistentDataType.STRING, player.uniqueId.toString())
        container.setLock(player.uniqueId.toString())
        player.sendMessage("Successfully locked.")
        player.sendMessage("with key: ${container.lock}")
    }

    /**
     * Unlocks a container and erases propriety.
     * @param container, the Container to unlock
     */
    private fun unlock(container: Lockable) {
        (container as Container).persistentDataContainer.remove(namespace)
        container.setLock("")
    }

    /**
     * Checks if a player owns a container.
     * @param container, the Container
     * @param player, the potential owner
     * @return true if the player owns the container, false otherwise.
     */
    private fun isOwner(container: Lockable, player: Player): Boolean {
        val uuids = (container as Container).persistentDataContainer.get(namespace, PersistentDataType.STRING) ?: return false
        return (UUID.fromString(uuids.split(",")[0]) == player.uniqueId)
    }

    /**
     * Allows another player to interact with a locked container.
     * @param container, the locked Container
     * @param player, the player to trust
     */
    private fun addNeighbor(container: Block, player: Player) {
        val uuids: List<Player> = (container as Container).persistentDataContainer.get(namespace, PersistentDataType.STRING)?.let {
                uniqueIds -> uniqueIds.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) }
        } ?: listOf()
        val uuidsStr = uuids.joinToString(",")
        (container.state as Container).persistentDataContainer.set(namespace, PersistentDataType.STRING, uuidsStr)
        uuids[0].sendMessage("${player.name} est devenu un membre de confiance pour ce bloc.")
        player.sendMessage("Vous êtes devenu un membre de confiance pour ${uuids[0]}.")
    }

    /**
     * Forbids a trusted player to interact with a locked container.
     * @param container, the Container
     * @param player, the player to withdraw trust
     */
    private fun removeNeighbor(container: Block, player: Player) {
        val uuids: List<Player> = (container as Container).persistentDataContainer.get(namespace, PersistentDataType.STRING)?.let {
                uniqueIds -> uniqueIds.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) }
        } ?: listOf()
        uuids.minus(player)
        val uuidsStr = uuids.joinToString(",")
        (container.state as Container).persistentDataContainer.set(namespace, PersistentDataType.STRING, uuidsStr)
        uuids[0].sendMessage("${player.name} n'est plus un membre de confiance pour ce bloc.")
        player.sendMessage("Vous n'êtes plus un membre de confiance pour ${uuids[0]}.")
    }
}
