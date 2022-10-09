package fr.pickaria.menu

import org.bukkit.entity.HumanEntity

abstract class BaseMenuFactory(val title: String) {
	abstract fun create(
		title: String = this.title,
		opener: HumanEntity? = null,
		previousMenu: BaseMenu? = null,
		size: Int = 54
	): BaseMenu
}