package fr.pickaria.deepslate

import fr.pickaria.menu.BuilderInit
import fr.pickaria.menu.Entry
import fr.pickaria.menu.Lore
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal val homeEntries = mutableListOf<HomeEntry>()

fun Entry.addToHome(material: Material, title: Component, lore: BuilderInit<Lore>): Entry {
	homeEntries.add(HomeEntry(material, title, Lore(lore).build(), this))
	return this
}