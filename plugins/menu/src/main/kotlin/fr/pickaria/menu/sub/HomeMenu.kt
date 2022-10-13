package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.menuController
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class HomeMenu(title: String, opener: HumanEntity, previousMenu: BaseMenu?, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	class Factory: BaseMenu.Factory("§6§lAccueil", Material.GRASS_BLOCK) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?, size: Int): BaseMenu = HomeMenu(title, opener, previousMenu, size)
	}

	override fun initMenu() {
		var x = 1

		menuController.menus.forEach { (key, menu) ->
			setMenuItem {
				this.x = x++
				y = 1
				material = menu.icon
				name = menu.title
				lore = menu.description
				callback = {
					menuController.openMenu(it.whoClicked, key, this@HomeMenu)
				}
			}
		}
	}
}