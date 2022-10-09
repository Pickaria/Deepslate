package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.BaseMenuFactory
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class HomeMenuFactory: BaseMenuFactory("§6§lAccueil", Material.GRASS_BLOCK) {
	override fun create(opener: HumanEntity?, previousMenu: BaseMenu?, size: Int): BaseMenu = HomeMenu(title, opener, previousMenu, size)
}