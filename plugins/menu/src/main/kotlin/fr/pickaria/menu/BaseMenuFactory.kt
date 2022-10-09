package fr.pickaria.menu

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

abstract class BaseMenuFactory(val title: String, val icon: Material = Material.AIR, vararg lore: String) {
	val description = listOf(*lore)

	abstract fun create(
		opener: HumanEntity? = null,
		previousMenu: BaseMenu? = null,
		size: Int = 54
	): BaseMenu
}