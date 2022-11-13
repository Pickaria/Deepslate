package fr.pickaria.economy

import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

abstract class Currency : Listener {
	protected abstract val material: Material
	protected abstract val description: List<String>
	protected abstract val currencyNameSingular: String
	protected abstract val currencyNamePlural: String
	protected open val account: String = "default"
	protected open val format: String = "0.00"

	private val currencyDisplayName: Component by lazy {
		Component.text(currencyNameSingular.replaceFirstChar {
			if (it.isLowerCase()) it.titlecase(
				Locale.getDefault()
			) else it.toString()
		}, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
	}

	val economy: Economy by lazy {
		Economy(currencyNameSingular, currencyNamePlural, account, format)
	}

	fun createItem(amount: Int = 1, value: Double = 1.0): ItemStack {
		val itemStack = ItemStack(material, amount)

		itemStack.itemMeta = itemStack.itemMeta.apply {
			addEnchant(GlowEnchantment.instance, 1, true)
			displayName(currencyDisplayName)

			val line = description.map {
				miniMessage.deserialize(it, Placeholder.unparsed("value", economy.format(value)))
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			}

			lore(line)

			persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, economy.account)
			persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, value)
		}

		return itemStack
	}
}