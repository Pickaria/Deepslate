package fr.pickaria.lock

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.Lockable
import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.persistence.PersistentDataType
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
 *   + checking UUIDs _and_ Nicknames is safer since nicknames can be changed at any time, so double the safety.
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
            player.sendMessage("$block\n\n$bState")
            if (bState is TileState) {
                if (isLocked(bState)) {
                    player.sendMessage("Ce ${block.type} appartient à ${getData(bState)?.split(",")?.get(-1)}.")
                }
                else {
                    lock(block, player)
                    player.sendMessage("Ce ${block.type} vous appartient désormais.")
                }
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
            if (bState is TileState) {
                if (isLocked(bState)) {
                    if (player.gameMode == GameMode.CREATIVE || isOwner(block, player)) {
                        player.sendMessage("Ce bloc n'est plus verrouillé.")
                        unlock(block)
                    }
                    else {
                        player.sendMessage("Vous ne pouvez pas faire ça. (propriété de ${getData(bState)?.split(",")?.get(-1)}).")
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
        /*
        val bState = container.state // capture once
        (bState as Lockable).setLock(player.uniqueId.toString())
        bState.update()
         */



        if (container.state is TileState) {
            setData(container.state as TileState, player)
        }
    }

    /**
     * Unlocks a container by erasing its property.
     * @param container, the Container to unlock
     */
    private fun unlock(container: Block) {
        /*
        val bState = container.state // capture once
        (bState as Lockable).setLock("")
        bState.update()
         */


        if (container.state is TileState) {
            (container.state as TileState).persistentDataContainer.remove(key)
        }
    }

    private fun isLocked(container: TileState): Boolean {
        return getData(container) != null
    }

    private fun getData(container: TileState): String? {
        val data = container.persistentDataContainer
        if (data.has(key, PersistentDataType.STRING)) {
            return data.get(key, PersistentDataType.STRING)
        }
        return ""
    }

    private fun setData(container: TileState, player: Player) {
        container.persistentDataContainer.set(key, PersistentDataType.STRING, player.uniqueId.toString()+","+player.name)
        container.update()
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
