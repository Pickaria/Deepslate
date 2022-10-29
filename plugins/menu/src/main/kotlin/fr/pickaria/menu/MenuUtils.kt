package fr.pickaria.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun createMenuItem(material: Material = Material.AIR, name: Component? = null, lore: List<Component>? = null, amount: Int = 1): ItemStack {
    val itemStack = ItemStack(material, amount)

    itemStack.itemMeta = itemStack.itemMeta?.apply {
        name?.let {
            this.displayName(it.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
        }

        lore?.let {
            this.lore(it)
        }
    }

    return itemStack
}