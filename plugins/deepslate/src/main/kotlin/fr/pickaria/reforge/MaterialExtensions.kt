package fr.pickaria.reforge

import org.bukkit.Material

// FIXME: Condition might not work in all cases
val Material.canBeEnchanted: Boolean
	get() = maxDurability != (0).toShort()