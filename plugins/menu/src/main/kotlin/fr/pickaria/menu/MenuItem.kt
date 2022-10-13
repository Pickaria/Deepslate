package fr.pickaria.menu

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class MenuItem {
	var name: String = ""
	var x: Int = 0
	var y: Int = 0
	var material: Material = Material.AIR
	var lore: List<String> = emptyList()
	var callback: ((InventoryClickEvent) -> Unit)? = null
	var isEnchanted: Boolean = false
	private lateinit var itemStack: ItemStack

	companion object {
		inline fun build(block: MenuItem.() -> Unit) = MenuItem().apply(block).build()
	}

	fun build(): MenuItem {
		itemStack = createMenuItem(material, name, lore)

		if (isEnchanted) {
			itemStack.addUnsafeEnchantment(Enchantment.MENDING, 1)
		}

		itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)

		return this
	}

	fun getItemStack() = this.itemStack
}