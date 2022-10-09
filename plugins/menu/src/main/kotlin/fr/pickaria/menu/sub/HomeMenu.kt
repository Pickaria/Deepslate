package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuItem
import fr.pickaria.menu.menuController
import org.bukkit.entity.HumanEntity

class HomeMenu(title: String, opener: HumanEntity?, previousMenu: BaseMenu?, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	override fun initMenu() {
		var x = 1

		menuController.menus.forEach { (key, menu) ->
			super.setMenuItem(x++, 1, MenuItem(menu.icon, menu.title, menu.description)
				.setCallback { event ->
					menuController.openMenu(event.whoClicked, key, this)
				}
			)
		}
	}
}