package fr.pickaria.menu

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class MenuItem {
	var name: Component = Component.empty()
	var x: Int = 0
	var y: Int = 0
	var material: Material = Material.AIR
	var lore: List<Component> = emptyList()
	var rightClick: ((InventoryClickEvent) -> Unit)? = null
	var leftClick: ((InventoryClickEvent) -> Unit)? = null
	var isEnchanted: Boolean = false
	var itemStack: ItemStack? = null

	companion object {
		inline fun build(block: MenuItem.() -> Unit) = MenuItem().apply(block).build()
	}

	fun build(): MenuItem {
		if (itemStack == null) {
			itemStack = createMenuItem(material, name, lore).apply {
				if (isEnchanted) {
					addUnsafeEnchantment(Enchantment.MENDING, 1)
				}

				if (!material.isAir) {
					addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
				}
			}
		}

		return this
	}
}