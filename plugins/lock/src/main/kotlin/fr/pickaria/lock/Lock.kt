package fr.pickaria.lock

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent

/**
 *     #--------- WORK IN PROGRESS
 *
 * 'Lock' is made for protecting containers.
 *   - Locked containers have a sign against one of the face.
 * A locked container have an owner.
 *   - The owner can have trusted players on this container.
 *   - Only the owner can break the protection.
 *   - Trusted members can access the container, but not break the protection.
 */

class Lock : Listener {
    /**
     * Protection plugin for all containers.
     */
    companion object {
        // header of the sign, indicates that the container is locked
        const val lockWord = "§6§1[Protected]"
        val lockBlocks = setOf(
            Material.BARREL,
            Material.BEACON,
            Material.BLAST_FURNACE,
            Material.BREWING_STAND,
            Material.CHEST,
            Material.DISPENSER,
            Material.DROPPER,
            Material.ENDER_CHEST,
            Material.FURNACE,
            Material.HOPPER,
            Material.SHULKER_BOX,
            Material.SMOKER,
            Material.TRAPPED_CHEST,
        )
        val lockSigns = setOf(Tag.SIGNS)
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockDamage(event: BlockDamageEvent) {
        /**
         * When a player tries to interact with a container, this function is called.
         */
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE) return

        val block = event.block
        when(block.type) {
            in lockSigns -> {}
            !in lockBlocks -> return
            else -> {}
        }
        player.sendMessage("hit Lockable....")
        if (player.isSneaking) {
            player.sendMessage("Lock is triggered.")
            when (isLocked(block)) {
                true -> unlock(block, player)
                false -> lock(block, player)
            }
        }
    }

    private fun isLocked(block: Block): Boolean {
        return (getKey() != null)
    }

    private fun getKey() {}

    private fun setKey() {}

    private fun lock(block: Block, player: Player) {
        // TODO: Create a sign indicating protection + owner.

        player.sendMessage("Locked.")
    }

    private fun unlock(block: Block, player: Player) {
        player.sendMessage("Unlocked.")
    }

}
