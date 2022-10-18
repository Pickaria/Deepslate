package fr.pickaria.shop

import fr.pickaria.artefact.getArtefact
import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor
import kotlin.math.min

internal fun createPickarite(amount: Int): ItemStack {
	val itemStack = ItemStack(Material.ECHO_SHARD, amount)

	itemStack.itemMeta = itemStack.itemMeta.apply {
		addEnchant(GlowEnchantment.instance, 1, true)
		displayName(
			Component.text("Éclat de Pickarite", NamedTextColor.LIGHT_PURPLE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)
		lore(
			listOf(
				Component.text("Fragment ancien issu de la", NamedTextColor.GRAY)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				Component.text("création du monde de Pickaria.", NamedTextColor.GRAY)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			)
		)

		persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)
	}

	return itemStack
}

internal fun isPickarite(item: ItemStack): Boolean =
	item.itemMeta?.persistentDataContainer?.get(namespace, PersistentDataType.BYTE)?.let { it == (1).toByte() } ?: false


internal fun createChestMerchant(player: Player, inventory: Inventory) {
	val money = economy.getBalance(player)

	val merchant = Bukkit.createMerchant(
		Component.text("Marché de Pickaria")
	)

	val recipes = inventory.contents
		.filterNotNull()
		.filter {
			!it.type.isAir
		}
		.toSet()

	merchant.recipes = recipes.map {
		val price: Int = getArtefact(it)?.value ?: (floor(Math.random() * 64) + 1).toInt()

		// Maximum the player can buy or maximum amount in stock
		val canBuy = min(floor(money / price).toInt(), it.amount)
		val merchantRecipe = MerchantRecipe(it.clone().apply { amount = 1 }, canBuy).apply {
			uses = 0
		}

		merchantRecipe.addIngredient(createPickarite(price.toInt()))

		merchantRecipe
	}

	player.openMerchant(merchant, true)
}