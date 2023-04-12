package fr.pickaria.controller.home

import fr.pickaria.menu.BuilderInit
import fr.pickaria.menu.Entry
import fr.pickaria.menu.Lore
import fr.pickaria.model.home.HomeEntry
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val homeEntries = mutableListOf<HomeEntry>()

fun Entry.addToHome(material: Material, title: Component, lore: BuilderInit<Lore>): Entry {
	val item = ItemStack(material).apply {
		editMeta {
			it.displayName(title)
			it.lore(Lore(lore).build())
		}
	}
	homeEntries.add(HomeEntry(item, this))
	return this
}

fun Entry.addToHome(item: ItemStack): Entry {
	homeEntries.add(HomeEntry(item, this))
	return this
}