package fr.pickaria.shard

import fr.pickaria.artefact.createArtefactReceptacle
import fr.pickaria.artefact.getArtefactConfig
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.math.floor

/**
 * Creates a new shard ItemStack.
 */
@Deprecated("Replace with Shard.createItem(amount)", ReplaceWith("Shard.createItem(amount)"))
fun createShardItem(amount: Int = 1): ItemStack = Shard.item(amount)

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
			addIngredient(Shard.item(price))
		}
	}

	return player.openMerchant(merchant, true)
}

/**
 * Returns the amount of Shards a player has, defaults to 0.
 */
@Deprecated(
	"Implement CurrencyExtensions and use `player.balance` instead.",
	ReplaceWith("Shard.economy.getBalance(player).toInt()")
)
fun getShardBalance(player: OfflinePlayer): Int = Shard.economy.getBalance(player).toInt()