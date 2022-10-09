package fr.pickaria.menu

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun createMenuItem(material: Material?, name: String?, vararg lore: String): ItemStack {
    val itemStack = ItemStack(material!!, 1)

    itemStack.itemMeta = itemStack.itemMeta!!.apply{
        name?.let {
            this.displayName(Component.text(it))
        }

        lore.let {
            this.lore(lore.map {
                Component.text(it)
            })
        }
    }

    return itemStack
}