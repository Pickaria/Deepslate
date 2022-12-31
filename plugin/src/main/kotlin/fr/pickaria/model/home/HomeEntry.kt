package fr.pickaria.model.home

import fr.pickaria.menu.Entry
import org.bukkit.inventory.ItemStack

data class HomeEntry(
	val itemStack: ItemStack,
	val entry: Entry,
)
