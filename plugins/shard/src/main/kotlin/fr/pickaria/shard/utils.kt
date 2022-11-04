package fr.pickaria.shard

import fr.pickaria.artefact.createArtefactReceptacle
import fr.pickaria.artefact.getArtefactConfig
import fr.pickaria.shared.GlowEnchantment
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor

/**
 * Creates a new shard ItemStack.
 */
fun createShardItem(amount: Int): ItemStack {
	val itemStack = ItemStack(Material.ECHO_SHARD, amount)

	itemStack.itemMeta = itemStack.itemMeta.apply {
		addEnchant(GlowEnchantment.instance, 1, true)
		displayName(shopConfig.pickariteLabel)
		lore(shopConfig.pickariteLore)

		persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)
	}

	return itemStack
}

/**
 * Returns true if the provided ItemStack is a valid shard.
 */
internal fun isShardItem(item: ItemStack): Boolean =
	item.itemMeta?.let {
		val isShard = it.persistentDataContainer.get(namespace, PersistentDataType.BYTE) == (1).toByte()
		val isEnchanted = it.hasEnchants() && it.enchants.contains(GlowEnchantment.instance)
		isShard && isEnchanted
	} ?: false

internal fun createChestMerchant(player: Player): InventoryView? {
	val money = getShardBalance(player)

	val merchant = Bukkit.createMerchant(shopConfig.shopName)

	val artefacts = artefactConfig?.artefacts ?: mapOf()

	merchant.recipes = artefacts.map { (_, config) ->
		val item = createArtefactReceptacle(config)
		val price: Int = getArtefactConfig(item)?.value ?: (floor(Math.random() * 64) + 1).toInt()

		// Maximum the player can buy
		val canBuy = money / price

		MerchantRecipe(item.clone().apply { amount = 1 }, canBuy).apply {
			uses = 0
			addIngredient(createShardItem(price))
		}
	}

	return player.openMerchant(merchant, true)
}

/**
 * Returns the amount of Shards a player has, defaults to 0.
 */
fun getShardBalance(player: OfflinePlayer): Int = economy.getBalance(player).toInt()