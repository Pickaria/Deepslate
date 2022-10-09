package fr.pickaria.job

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.BaseMenuFactory
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class JobMenuFactory: BaseMenuFactory("§6§lMétiers", Material.WOODEN_PICKAXE) {
	override fun create(opener: HumanEntity?, previousMenu: BaseMenu?, size: Int): BaseMenu = JobsMenu(title, opener, previousMenu, size)
}