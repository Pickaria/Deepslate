package fr.pickaria.shard

import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

/**
 * Creates a new shard ItemStack.
 */
@Deprecated("Replace with Shard.createItem(amount)", ReplaceWith("Shard.createItem(amount)"))
fun createShardItem(amount: Int = 1): ItemStack = Shard.item(amount)

/**
 * Returns the amount of Shards a player has, defaults to 0.
 */
@Deprecated(
	"Implement CurrencyExtensions and use `player.balance` instead.",
	ReplaceWith("Shard.economy.getBalance(player).toInt()")
)
fun getShardBalance(player: OfflinePlayer): Int = Shard.economy.getBalance(player).toInt()