package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.BaseMenuFactory
import org.bukkit.entity.HumanEntity

class HomeMenuFactory: BaseMenuFactory("§6§lAccueil") {
	override fun create(title: String, opener: HumanEntity?, previousMenu: BaseMenu?, size: Int) = HomeMenu(title, opener, previousMenu, size)
}