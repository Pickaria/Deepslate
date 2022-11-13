package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

internal class Listeners : Listener, CurrencyExtensions(Credit) {
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
								whoClicked.playSound(sound)
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
}