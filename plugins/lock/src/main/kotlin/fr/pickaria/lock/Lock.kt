package fr.pickaria.lock

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.block.Lockable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import java.util.*

/**
 * #####----- WORK IN PROGRESS
 *
 * 'Lock' is made for Lockable containers.
 *   - A locked container have an owner.
 *   - The owner can have trusted players on its container.
 *   - Only the owner can break the protection.
 *   - Trusted members can access the container, but not break the protection.
 *
 *   + checking UUIDs instead of nicknames is safer since nicknames can be changed at any time.
 */
class Lock : Listener {
    /**
     * Called when a player tries to punch a block.
     */
    @EventHandler
    fun onBlockDamage(event: BlockDamageEvent) {
        with(event) {
            if (!player.isSneaking) return
            val bState = block.state // capture once
            if (bState is Lockable) {
                val bLockable = (bState as Lockable)
                if (!bLockable.isLocked) {
                    lock(block, player)
                    player.sendMessage("Ce ${block.type} vous appartient désormais.")
                }
                else
                    player.sendMessage("Ce ${block.type} appartient à ${Bukkit.getPlayer(UUID.fromString(bLockable.lock))?.name}.")
            }
        }
    }

    /**
     * Called when a player tries to totally break a block.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        //TODO: control thieves and bypasses (one-click break?)
        with(event) {
            val bState = block.state // capture once
            if (bState is Lockable) {
                if ((bState as Lockable).isLocked) {
                    if (player.gameMode == GameMode.CREATIVE || isOwner(block, player)) {
                        player.sendMessage("Ce bloc était verrouillé.")
                        unlock(block)
                        return
                    }
                    else {
                        player.sendMessage("Vous ne pouvez pas faire ça. (propriété de ${Bukkit.getPlayer(UUID.fromString((bState as Lockable).lock))?.name}).")
                        isCancelled = true
                    }
                }
            }
        }
    }

    /**
     * Locks a container and sets a player as its owner.
     * @param container, the Container to lock
     * @param player, the owner
     */
    private fun lock(container: Block, player: Player) {
        val bState = container.state // capture once
        (bState as Lockable).setLock(player.uniqueId.toString())
        bState.update()
    }

    /**
     * Unlocks a container by erasing its property.
     * @param container, the Container to unlock
     */
    private fun unlock(container: Block) {
        val bState = container.state // capture once
        (bState as Lockable).setLock("")
        bState.update()
    }

    /**
     * Checks if a player owns a locked container.
     * @param container, the locked container
     * @param player, the potential owner
     * @return true if the player owns the container, false otherwise.
     */
    private fun isOwner(container: Block, player: Player): Boolean {
        val uuids: List<Player> = (container.state as Lockable).lock.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) }
        return (uuids[0].uniqueId == player.uniqueId)
    }


    /*
    ******************** FUTURE FEATURES BELOW ********************.
    */

    /**
     * Allows another player to interact with a locked container.
     * @param container, the locked container
     * @param player, the player to trust
     */
    private fun addNeighbor(container: Block, player: Player) {
        val bState = container.state
        val uuids: List<Player> = (bState as Lockable).lock.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) } + player
        (bState as Lockable).setLock(uuids.joinToString(","))
        uuids[0].sendMessage("${player.name} est devenu un membre de confiance pour ce bloc.")
        player.sendMessage("${uuids[0].name} vous donne un accès libre: ${container.type}, X=${container.x}, Y=${container.y}, Z=${container.z}.")
    }

    /**
     * Withdraws interaction with a locked container to a player already trusted.
     * @param container, the locked container
     * @param player, the player to withdraw trust
     */
    private fun removeNeighbor(container: Block, player: Player) {
        val bState = container.state
        val uuids: List<Player> = (bState as Lockable).lock.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) } - player
        (bState as Lockable).setLock(uuids.joinToString(","))
        uuids[0].sendMessage("${player.name} n'est plus un membre de confiance pour ce bloc.")
        player.sendMessage("${uuids[0].name} vous retire un accès libre: ${container.type}, X=${container.x}, Y=${container.y}, Z=${container.z}.")
    }
}
