package fr.pickaria.vue.chat

import fr.pickaria.DEFAULT_MENU
import fr.pickaria.menu.isInMenu
import fr.pickaria.menu.open
import fr.pickaria.shared.MiniMessage
import fr.pickaria.shared.give
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class FirstJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        with(event) {
            if (!player.hasPlayedBefore()) {
                player.give(ItemStack(Material.COMPASS))
                player.server.broadcast(MiniMessage("<gray>Bienvenue à <gold><name><gray> sur le serveur !") {
                    "name" to player.displayName()
                }.toComponent())
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        with(event) {
            if (useItemInHand() != Event.Result.DENY && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && player.inventory.itemInMainHand.type == Material.COMPASS && !player.isInMenu()) {
                player.open(DEFAULT_MENU)
                isCancelled = true
                setUseItemInHand(Event.Result.DENY)
            }
        }
    }
}