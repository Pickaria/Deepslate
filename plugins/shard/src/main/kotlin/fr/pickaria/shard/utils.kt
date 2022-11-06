package fr.pickaria.shard

import fr.pickaria.artefact.createArtefactReceptacle
import fr.pickaria.artefact.getArtefactConfig
import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor

/**
 * Creates a new shard ItemStack.
 */
fun createShardItem(amount: Int = 1): ItemStack {
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
fun ItemStack.isShard(): Boolean =
	itemMeta?.let {
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

/**
 * If the given item is a valid shard item, credits the amount to the player's account and remove the items.
 */
fun creditShard(item: ItemStack, player: Player): Boolean =
	if (item.isShard()) {
		val amount = item.amount.toDouble()
		economy.depositPlayer(player, amount)

		// Just some feedback
		val placeholder = Placeholder.unparsed("amount", economy.format(amount))
		val message = miniMessage.deserialize(shopConfig.collectShardMessage, placeholder)
		player.sendMessage(message)

		player.playSound(shopConfig.grindSound)
		player.eyeLocation.let {
			it.world.spawnParticle(Particle.END_ROD, it.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
		}

		item.amount = 0
		true
	} else {
		false
	}