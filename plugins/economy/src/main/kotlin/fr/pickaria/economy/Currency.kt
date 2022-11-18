package fr.pickaria.economy

import fr.pickaria.shared.ConfigProvider
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class Currency : ConfigProvider() {
	val material: Material by this
	val description: List<String> by this
	private val nameSingular: String by this
	private val namePlural: String by this
	val account: String by this
	val format: String by this
	private val collectMessage: String by this
	private val collectSound: Sound by this

	private val currencyDisplayName: Component by lazy {
		Component.text(nameSingular.replaceFirstChar {
			if (it.isLowerCase()) it.titlecase(
				Locale.getDefault()
			) else it.toString()
		}, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
	}

	val economy: Economy by lazy {
		Economy(nameSingular, namePlural, account, format)
	}

	/**
	 * Creates a new ItemStack for the currency.
	 * @param amount Size of the stack, must be between 0 and `Material.maxStackSize`, defaults to 1.
	 * @param value Value of each currency item, defaults to 1.0.
	 */
	fun item(amount: Int = 1, value: Double = 1.0): ItemStack {
		val itemStack = ItemStack(material, amount)

		itemStack.editMeta { meta ->
			meta.addEnchant(GlowEnchantment.instance, 1, true)
			meta.displayName(currencyDisplayName)

			val line = description.map {
				miniMessage.deserialize(it, Placeholder.unparsed("value", economy.format(value)))
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			}

			meta.lore(line)

			meta.persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, account)
			meta.persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, value)
		}

		return itemStack
	}

	fun collect(player: OfflinePlayer, itemStack: ItemStack): EconomyResponse =
		if (itemStack.account == account) {
			player.deposit(this, itemStack.totalValue).also {
				itemStack.amount = 0

				if (player is Player) {
					MiniMessage(collectMessage) {
						"amount" to economy.format(it.amount)
					}.send(player)
					player.playSound(collectSound)
				}
			}
		} else {
			EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Tried to deposit an item that is not a currency.")
		}
}