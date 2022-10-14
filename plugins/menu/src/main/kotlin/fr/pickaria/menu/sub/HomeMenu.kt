package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.menuController
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class HomeMenu(title: String, opener: HumanEntity, previousMenu: BaseMenu?, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	init {
		fillMaterial = Material.WHITE_STAINED_GLASS_PANE
	}

	class Factory: BaseMenu.Factory("§6§lAccueil", Material.GRASS_BLOCK) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?, size: Int): BaseMenu = HomeMenu(title, opener, previousMenu, size)
	}

	override fun initMenu() {
		var x = 1
		var y = 1

		menuController.menus.forEach { (key, menu) ->
			setMenuItem {
				this.x = x
				this.y = y
				material = menu.icon
				name = menu.title
				lore = menu.description
				leftClick = {
					menuController.openMenu(it.whoClicked, key, this@HomeMenu)
				}
			}

			x += 2

			if (x > 7) {
				y++

				if (y % 4 == 0) {
					y += 2
				}

				x = 1
			}
		}
	}
}