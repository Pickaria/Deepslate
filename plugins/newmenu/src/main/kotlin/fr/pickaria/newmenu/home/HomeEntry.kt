package fr.pickaria.newmenu.home

import fr.pickaria.newmenu.Entry
import net.kyori.adventure.text.Component
import org.bukkit.Material

data class HomeEntry(
	val material: Material,
	val title: Component,
	val lore: MutableList<Component>,
	val entry: Entry,
)
