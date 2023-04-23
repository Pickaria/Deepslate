package fr.pickaria.model.rtp

import fr.pickaria.menu.Entry
import org.bukkit.inventory.ItemStack

data class RtpEntry(
	val itemStack: ItemStack,
	val entry: Entry,
)
