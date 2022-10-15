package fr.pickaria.shop

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.math.floor

fun createChestMerchant(player: Player, inventory: Inventory) {
	val merchant = Bukkit.createMerchant(
		Component.text("Magasin ", NamedTextColor.DARK_GRAY)
			.append(
				Component.text(
					economy.format(economy.getBalance(player as OfflinePlayer)),
					NamedTextColor.GOLD,
					TextDecoration.BOLD
				)
			)
	)

	val recipes = inventory.contents
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

	merchant.recipes = recipes.map {
		val price = floor(Math.random() * 64) + 1

		val merchantRecipe = MerchantRecipe(it, Integer.MAX_VALUE)
		merchantRecipe.uses = Integer.MIN_VALUE

		val itemStack = ItemStack(
			Material.SUNFLOWER,
			price.toInt()
		)

		itemStack.itemMeta = itemStack.itemMeta.apply {
			displayName(
				Component.text("Prix : ", NamedTextColor.GRAY)
					.append(Component.text(economy.format(price), NamedTextColor.GOLD, TextDecoration.BOLD))
			)
		}

		merchantRecipe.addIngredient(itemStack)

		merchantRecipe
	}

	player.openMerchant(merchant, true)
}