package fr.pickaria.shop

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.TradeSelectEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.math.floor

class TestMenu : Listener {
	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		event.whoClicked.sendMessage("${event.index}")
	}

	@EventHandler
	fun onChestOpened(event: InventoryOpenEvent) {
		if (event.player.isOnGround && event.inventory.type == InventoryType.CHEST) {
			val merchant = Bukkit.createMerchant(Component.text("Shop"))

			val recipes = event.inventory.contents
				.filterNotNull()
				.map {
					it.clone().apply {
						amount = 1
					}
				}
				.filter {
					!it.type.isAir
				}
				.toSet()

			merchant.recipes = recipes.mapNotNull {
				if (!it.type.isAir) {
					val merchantRecipe = MerchantRecipe(it, Integer.MAX_VALUE)
					merchantRecipe.uses = Integer.MIN_VALUE
					merchantRecipe.addIngredient(
						ItemStack(
							Material.ECHO_SHARD,
							(floor(Math.random() * 64) + 1).toInt()
						)
					)

					merchantRecipe
				} else {
					null
				}
			}

			event.player.openMerchant(merchant, true)

			event.isCancelled = true
		}
	}
}
