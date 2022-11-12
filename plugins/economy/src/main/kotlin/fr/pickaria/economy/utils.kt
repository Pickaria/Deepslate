package fr.pickaria.economy

import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Creates a new coin ItemStack.
 */
fun createCoinItem(value: Double = 1.0, amount: Int = 1): ItemStack {
	val itemStack = ItemStack(Material.GOLD_NUGGET, amount)

	itemStack.itemMeta = itemStack.itemMeta.apply {
		addEnchant(GlowEnchantment.instance, 1, true)
		displayName(
			Component.text(economy.format(value * amount))
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)
		lore(
			listOf(
				miniMessage.deserialize(Config.coinDescription, Placeholder.unparsed("value", value.toString()))
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			)
		)

		persistentDataContainer.set(namespace, PersistentDataType.DOUBLE, value)
	}

	return itemStack
}

/**
 * Returns true if the provided ItemStack is a valid coin item.
 */
fun ItemStack.isCoin(): Boolean =
	itemMeta?.let {
		val isCoin = it.persistentDataContainer.has(namespace, PersistentDataType.DOUBLE)
		val isEnchanted = it.hasEnchants() && it.enchants.contains(GlowEnchantment.instance)
		isCoin && isEnchanted
	} ?: false

/**
 * Returns the value of the coin item, returns 0 if it is not a coin.
 */
val ItemStack.coinValue: Double
	get() = if (isCoin()) {
		itemMeta?.persistentDataContainer?.get(namespace, PersistentDataType.DOUBLE) ?: 0.0
	} else {
		0.0
	}

val ItemStack.totalCoinValue: Double
	get() = coinValue * amount

/**
 * If the given item is a valid shard item, credits the amount to the player's account and remove the items.
 */
fun creditCoin(item: ItemStack, player: Player): Boolean =
	if (item.isCoin()) {
		val value = item.totalCoinValue
		economy.depositPlayer(player, value)

		// Just some feedback
		val placeholder = Placeholder.unparsed("amount", economy.format(value))
		val message = miniMessage.deserialize(Config.coinCollectMessage, placeholder)
		player.sendMessage(message)

		val sound = Sound.sound(Key.key(Config.coinCollectSound), Sound.Source.MASTER, 1f, 1f)
		player.playSound(sound)

		item.amount = 0
		true
	} else {
		false
	}

fun ItemStack.adjustCoin() {
	if (isCoin()) {
		editMeta {
			it.displayName(
				Component.text(economy.format(coinValue * amount))
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			)
		}
	}
}