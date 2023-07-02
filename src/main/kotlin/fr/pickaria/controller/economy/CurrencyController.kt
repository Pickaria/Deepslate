package fr.pickaria.controller.economy

import fr.pickaria.controller.market.overflowStacks
import fr.pickaria.model.economy.*
import fr.pickaria.model.economy.Currency
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import org.bukkit.persistence.PersistentDataType
import java.text.DecimalFormat
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
	 * All the physical currencies sorted by descending value.
	 */
	private val physicalCurrencies by lazy {
		model.physicalCurrencies.sortedByDescending { it.value }
	}

	/**
	 * Get the items required to match a specified total value.
	 */
	fun items(totalValue: Double): List<ItemStack> {
		var remaining = totalValue
		val coins = mutableMapOf<PhysicalCurrency, Int>()

		physicalCurrencies.forEach {
			val amount = (remaining / it.value).toInt()
			coins[it] = amount
			remaining -= amount * it.value
		}

		return coins.flatMap { (physicalCurrency, amount) ->
			overflowStacks(physicalCurrency.material, amount) { meta ->
				meta.addEnchant(GlowEnchantment.instance, 1, true)
				meta.displayName(currencyDisplayName)

				val line = model.description.map {
					MiniMessage(it) {
						"value" to format(physicalCurrency.value)
					}.toComponent().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				}

				meta.lore(line)

				meta.persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, model.account)
				meta.persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, physicalCurrency.value)
			}
		}
	}

	/**
	 * Put all items for a specified total value in a bundle.
	 */
	fun bundle(totalValue: Double, items: List<ItemStack>) = ItemStack(Material.BUNDLE).apply {
		editMeta { meta ->
			meta.addEnchant(GlowEnchantment.instance, 1, true)

			val name = Component.text("Sacoche de")
				.append(Component.space())
				.append(currencyDisplayName)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

			meta.displayName(name)

			val line = MiniMessage(economyConfig.bundleDescription) {
				"value" to this@CurrencyController.format(totalValue)
			}.toComponent().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

			meta.lore(listOf(line))

			meta.persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, model.account)
			meta.persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, totalValue)

			meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

			items.forEach {
				(meta as BundleMeta).addItem(it)
			}
		}
	}

	/**
	 * Creates a bundle containing all items for a specified total value.
	 */
	fun bundle(totalValue: Double): ItemStack = bundle(totalValue, items(totalValue))

	/**
	 * Creates a new ItemStack for the currency.
	 * @param amount Size of the stack, must be between 0 and `Material.maxStackSize`, defaults to 1.
	 * @param value Value of each currency item, defaults to 1.0.
	 */
	@Deprecated("Use items(totalValue).first() instead")
	fun item(amount: Int = 1, value: Double = 1.0): ItemStack {
		val itemStack = ItemStack(model.physicalCurrencies.first().material, amount)

		itemStack.editMeta { meta ->
			meta.addEnchant(GlowEnchantment.instance, 1, true)
			meta.displayName(currencyDisplayName)

			val line = model.description.map {
				MiniMessage(it) {
					"value" to format(value)
				}.toComponent().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			}

			meta.lore(line)

			meta.persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, model.account)
			meta.persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, value)
		}

		return itemStack
	}

	fun item(totalValue: Double): ItemStack {
		val items = items(totalValue)

		return if (items.size > 1) {
			bundle(totalValue, items)
		} else {
			items.first()
		}
	}

	private val formatter = DecimalFormat(model.format).apply {
		decimalFormatSymbols = decimalFormatSymbols.apply {
			groupingSeparator = ' '
		}
	}

	fun format(amount: Double): String = if (amount <= 1.0) {
		"${formatter.format(amount)} ${model.nameSingular}"
	} else {
		"${formatter.format(amount)} ${model.namePlural}"
	}

	fun format(amount: Int): String = format(amount.toDouble())

	fun message(player: Player, amount: Double) {
		MiniMessage(model.collectMessage) {
			"amount" to format(amount)
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
				0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Tried to deposit an item that is not a currency."
			)
		}
}