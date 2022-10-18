package fr.pickaria.shop

import fr.pickaria.artefact.createArtefactReceptacle
import fr.pickaria.artefact.getArtefactConfig
import fr.pickaria.shared.GlowEnchantment
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor

internal fun createPickarite(amount: Int): ItemStack {
	val itemStack = ItemStack(Material.ECHO_SHARD, amount)

	itemStack.itemMeta = itemStack.itemMeta.apply {
		addEnchant(GlowEnchantment.instance, 1, true)
		displayName(shopConfig.pickariteLabel)
		lore(shopConfig.pickariteLore)

		persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)
	}

	return itemStack
}

internal fun isPickarite(item: ItemStack): Boolean =
	item.itemMeta?.persistentDataContainer?.get(namespace, PersistentDataType.BYTE)?.let { it == (1).toByte() } ?: false


internal fun createChestMerchant(player: Player) {
	val money = economy.getBalance(player)

	val merchant = Bukkit.createMerchant(shopConfig.shopName)

	val artefacts = artefactConfig?.artefacts ?: mapOf()

	merchant.recipes = artefacts.map { (_, config) ->
		val item = createArtefactReceptacle(config)
		val price: Int = getArtefactConfig(item)?.value ?: (floor(Math.random() * 64) + 1).toInt()

		// Maximum the player can buy
		val canBuy = floor(money / price).toInt()

		MerchantRecipe(item.clone().apply { amount = 1 }, canBuy).apply {
			uses = 0
			addIngredient(createPickarite(price))
		}
	}

	val view = player.openMerchant(merchant, true)

	view?.topInventory?.let {
		if( it.type == InventoryType.MERCHANT) {
			it.holder
		}
	}
}