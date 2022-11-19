package fr.pickaria.lock

import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Lockable
import org.bukkit.block.Sign
import org.bukkit.block.data.Directional
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

/**
 *     #--------- WORK IN PROGRESS
 *
 * 'Lock' is made for Lockable containers.
 *   - Locked containers have a sign against one of their faces (must be `Material.AIR`).
 *   - A locked container have an owner.
 *   - The owner can have trusted players on its container.
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
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockDamage(event: BlockDamageEvent) {
        /**
         * When a player tries to interact with a container, this function is called.
         */
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE || !player.isSneaking) return

        val block = event.block
        when (block.state) {
            is Lockable -> {
                val container = block.state as Lockable
                if (container.isLocked) unlock(block) else lock(block, player)
            }
            is Sign -> {
                val sign = block.state as Sign
                if (isProtectionSign(sign)) unlockFromProtectionSign(sign) else return
            }
            else -> return
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        //TODO: control thieves and bypasses (one-click break)
    }

    private fun isProtectionSign(sign: Sign): Boolean {
    return ((sign.line(0) == Component.text(lockWord))
            && (sign.line(1) != Component.text("")))
    }

    private fun lock(container: Block, player: Player) {
        player.sendMessage("Going to lock this Lockable....")
        val lookAtFace = player.getTargetBlockFace(5)!!
        player.sendMessage("looking on a face: $lookAtFace....")
        placeProtectionSign(container.getRelative(lookAtFace), lookAtFace, player)
        // last tasks!
        (container.blockData as PersistentDataContainer).set(NamespacedKey.fromString(container.location.toString())!!, PersistentDataType.STRING, player.uniqueId.toString())
        (container.state as Lockable).setLock(player.uniqueId.toString())
    }

    private fun unlock(container: Block) {
        //TODO: look for Protection Sign, especially for Double Chests.
        (container.state as Lockable).setLock("")
    }

    private fun unlockFromProtectionSign(sign: Sign) {}

    private fun placeProtectionSign(block: Block, face: BlockFace, player: Player) {
        player.sendMessage("trying to place a sign on: ${block.state.location}....")
        block.type = Material.ACACIA_WALL_SIGN
        val sign = block.state as Sign
        val signDir = (block.blockData as Directional)
        signDir.facing = face
        block.blockData = signDir
        sign.line(0, Component.text(lockWord))
        sign.line(1, Component.text(player.name))
        sign.update()
    }
}
