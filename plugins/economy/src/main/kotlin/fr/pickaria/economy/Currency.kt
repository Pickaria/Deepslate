package fr.pickaria.economy

import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

internal val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)
internal val currencyNamespace = NamespacedKey(plugin, "currency")
internal val valueNamespace = NamespacedKey(plugin, "value")
internal val currencies = mutableMapOf<String, Currency>()

abstract class Currency : Listener {
	abstract val economy: Economy
	protected abstract val material: Material
	protected abstract val currencyDisplayName: Component
	protected abstract val description: String
	open val sound: String? = null
	open val creditMessage: String? = null

	fun register() {
		currencies[economy.account] = this
	}

	fun createItem(amount: Int = 1, value: Double = 1.0): ItemStack {
		val itemStack = ItemStack(material, amount)

		itemStack.itemMeta = itemStack.itemMeta.apply {
			addEnchant(GlowEnchantment.instance, 1, true)
			displayName(currencyDisplayName)

			val line = miniMessage.deserialize(description, Placeholder.unparsed("value", economy.format(value)))
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			lore(listOf(line))

			persistentDataContainer.set(currencyNamespace, PersistentDataType.STRING, economy.account)
			persistentDataContainer.set(valueNamespace, PersistentDataType.DOUBLE, value)
		}

		return itemStack
	}
}