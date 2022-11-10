package fr.pickaria.newmenu.home

import fr.pickaria.newmenu.BuilderInit
import fr.pickaria.newmenu.Entry
import fr.pickaria.newmenu.Lore
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal val homeEntries = mutableListOf<HomeEntry>()

fun Entry.addToHome(material: Material, title: Component, lore: BuilderInit<Lore>): Entry {
	homeEntries.add(HomeEntry(material, title, Lore(lore).build(), this))
	return this
}