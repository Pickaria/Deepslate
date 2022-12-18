package fr.pickaria.home

import fr.pickaria.menu.Entry
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal data class HomeEntry(
	val material: Material,
	val title: Component,
	val lore: MutableList<Component>,
	val entry: Entry,
)