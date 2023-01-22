package fr.pickaria.lock

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * #######--- WORK IN PROGRESS
 *
 * 'Lock' is made for TileState blocks (basically, those which open a GUI, allows interaction).
 *   - A locked block have an owner.
 *   - The owner can have trusted players on its block.
 *   - Only the owner can break the protection.
 *   - Trusted members can access the block, but not break the protection.
 *   - Any other people trying to either break or access the block is denied.
 */
class Lock(pluginInstance: Main) : Listener {
    private val key = NamespacedKey(pluginInstance, "key")

    /**
     * Called when a player tries to punch a block.
     */
    @EventHandler
    fun onBlockDamage(event: BlockDamageEvent) {
        with(event) {
            if (!player.isSneaking) return
            val bState = block.state // capture once
            if (bState is TileState) {
                if (isLocked(bState)) {
                    if (isOwner(bState, player)) {
                        unlock(bState)
                        player.sendMessage("Ce ${block.type} vient d'être déverrouillé.")
                    }
                    else {
                        player.sendMessage("Ce ${block.type} appartient à ${getOwner(bState)?.name}.")
                    }
                }
                else {
                    lock(bState, player)
                    player.sendMessage("Ce ${block.type} vient d'être verrouillé.")
                }
            }
        }
    }

    /**
     * Called when a player tries to totally break a block.
     */
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        //TODO: control thieves and bypasses (one-click break?)
        with(event) {
            val bState = block.state // capture once
            if (bState is TileState && isLocked(bState)) {
                if (!((player.gameMode == GameMode.CREATIVE) || isOwner(bState, player))) {
                    player.sendMessage("Seul ${getOwner(bState)?.name} peut détruire ce ${block.type}.")
                    getOwner(bState)?.sendMessage("Attention, ${player.name} essaie de détruire un ${block.type} (${block.x}, ${block.y}, ${block.z}).")
                    isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        with(event) {
            val bState = clickedBlock?.state
            if (action.isRightClick && bState is TileState && isLocked(bState)) {
                if (!((player.gameMode == GameMode.CREATIVE) || isAllowed(bState, player))) {
                    player.sendMessage("Vous ne pouvez pas accéder à ce ${clickedBlock?.type}.")
                    getOwner(bState)?.sendMessage("Attention, ${player.name} essaie d'accéder à un ${clickedBlock?.type} (${clickedBlock?.x}, ${clickedBlock?.y}, ${clickedBlock?.z}).")
                    isCancelled = true
                    return
                }
            }
        }
    }

    /**
     * Locks a block and sets the player as owner.
     * @param block, the block to lock
     * @param player, the owner
     */
    private fun lock(block: TileState, player: Player) {
        block.persistentDataContainer.set(key, PersistentDataType.STRING, player.uniqueId.toString()+","+player.name+"\n")
        block.update()
    }

    /**
     * Unlocks a block by erasing its property.
     * @param block, the block to unlock
     */
    private fun unlock(block: TileState) {
        block.persistentDataContainer.remove(key)
        block.update()
    }

    /**
     * Checks if the block is locked.
     * @param block, the locked block
     * @return true if owned, false otherwise.
     */
    private fun isLocked(block: TileState): Boolean {
        return (block.persistentDataContainer.has(key, PersistentDataType.STRING))
    }

    /**
     * Gets the players allowed to interact with the locked block.
     * @param block, the locked block
     * @return the players allowed (if so).
     */

    private fun getData(block: TileState): String? {
        val data = block.persistentDataContainer
        if (data.has(key, PersistentDataType.STRING)) {
            return data.get(key, PersistentDataType.STRING)
        }
        return null
    }

    /**
     * Checks if a player owns a locked block.
     * @param block, the locked block
     * @param player, the potential owner
     * @return true if the player owns the block, false otherwise.
     */
    private fun isOwner(block: TileState, player: Player): Boolean {
        return getData(block)?.split("\n")?.get(0)?.split(",")?.any {
            it == player.name || it == player.uniqueId.toString()
        } ?: false
    }

    /**
     * Gets the player who owns the locked block.
     * @param block, the locked block
     * @return the player who owns the locked block.
     */
    private fun getOwner(block: TileState): Player? {
        return Bukkit.getPlayer(UUID.fromString(getData(block)?.split("\n")?.get(0)?.split(",")?.get(0)))
    }

    /**
     * Checks if a player is allowed to interact with a locked block.
     * @param block, the locked block
     * @param player, the player who wants to access the locked block
     * @return true if the player is allowed, false otherwise.
     */
    private fun isAllowed(block: TileState, player: Player): Boolean {
        // Extends 'isOwner' since the owner is OP on the block: the owner can access the block, of course.
        val data = getData(block)
        val dataList = data?.split("\n")
        if (dataList != null) {
            for (el in dataList) {
                if (el.split(",").any { it == player.name || it == player.uniqueId.toString() })
                    return true
            }
        }
        return false
    }

    /*
    ******************** FUTURE FEATURES BELOW ********************.
    */

    /*

    /**
     * Allows another player to interact with a locked block.
     * @param block, the locked block
     * @param player, the player to trust
     */
    private fun addNeighbor(block: Block, player: Player) {
        val bState = block.state
        val uuids: List<Player> = (bState as Lockable).lock.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) } + player
        (bState as Lockable).setLock(uuids.joinToString(","))
        uuids[0].sendMessage("${player.name} est devenu un membre de confiance pour ce bloc.")
        player.sendMessage("${uuids[0].name} vous donne un accès libre: ${block.type}, X=${block.x}, Y=${block.y}, Z=${block.z}.")
    }

    /**
     * Withdraws interaction with a locked block to a player already trusted.
     * @param block, the locked block
     * @param player, the player to withdraw trust
     */
    private fun removeNeighbor(block: Block, player: Player) {
        val bState = block.state
        val uuids: List<Player> = (bState as Lockable).lock.split(",").mapNotNull { Bukkit.getPlayer(UUID.fromString(it)) } - player
        (bState as Lockable).setLock(uuids.joinToString(","))
        uuids[0].sendMessage("${player.name} n'est plus un membre de confiance pour ce bloc.")
        player.sendMessage("${uuids[0].name} vous retire un accès libre: ${block.type}, X=${block.x}, Y=${block.y}, Z=${block.z}.")
    }

     */
}
