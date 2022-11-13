package fr.pickaria.economy

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryInteractEvent

internal class Listeners : Listener {
	@EventHandler
	fun onCurrencyClick(event: InventoryClickEvent) {
		with(event) {
			currentItem?.let {
				if (it.isCurrency()) {
					result = Event.Result.DENY
					isCancelled = true

					val response = (whoClicked as OfflinePlayer) deposit it

					if (response.type == EconomyResponse.ResponseType.SUCCESS) {
						it.currency?.let { currency ->
							currency.creditMessage?.let { message ->
								val placeholder = Placeholder.unparsed("amount", economy.format(it.totalValue))
								whoClicked.sendMessage(miniMessage.deserialize(message, placeholder))
							}

							currency.sound?.let { sound ->
								whoClicked.playSound(Sound.sound(Key.key(sound), Sound.Source.MASTER, 1f, 1f))
							}
						}

						it.amount = 0
					}
				}
			}
		}
	}

	@EventHandler
	fun onCurrencyDrag(event: InventoryDragEvent) {
		with(event) {
			if (oldCursor.isCurrency() || cursor?.isCurrency() == true) {
				result = Event.Result.DENY
				isCancelled = true
			}
		}
	}

	@EventHandler
	fun onCurrencyMove(event: InventoryInteractEvent) {
		Bukkit.broadcastMessage("event")
	}
}