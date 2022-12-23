package fr.pickaria.controller.economy

import fr.pickaria.model.economy.Currency
import fr.pickaria.model.economy.currencyNamespace
import fr.pickaria.model.economy.valueNamespace
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class CurrencyController(val model: Currency) {
	private val currencyDisplayName: Component by lazy {
		Component.text(model.nameSingular.replaceFirstChar {
			if (it.isLowerCase()) it.titlecase(
				Locale.getDefault()
			) else it.toString()
		}, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
	}

	/**
	 * Creates a new ItemStack for the currency.
	 * @param amount Size of the stack, must be between 0 and `Material.maxStackSize`, defaults to 1.
	 * @param value Value of each currency item, defaults to 1.0.
	 */
	fun item(amount: Int = 1, value: Double = 1.0): ItemStack {
		val itemStack = ItemStack(model.material, amount)

		itemStack.editMeta { meta ->
			meta.addEnchant(GlowEnchantment.instance, 1, true)
			meta.displayName(currencyDisplayName)

			val line = model.description.map {
				MiniMessage(it) {
					"value" to model.economy.format(value)
				}.message.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			}

			meta.lore(line)

			meta.persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, model.account)
			meta.persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, value)
		}

		return itemStack
	}

	fun message(player: Player, amount: Double) {
		MiniMessage(model.collectMessage) {
			"amount" to model.economy.format(amount)
		}.send(player)
		player.playSound(model.collectSound)
	}

	fun collect(player: OfflinePlayer, itemStack: ItemStack): EconomyResponse =
		if (itemStack.account == model.account) {
			player.deposit(model, itemStack.totalValue).also {
				itemStack.amount = 0
			}
		} else {
			EconomyResponse(
				0.0,
				0.0,
				EconomyResponse.ResponseType.FAILURE,
				"Tried to deposit an item that is not a currency."
			)
		}
}