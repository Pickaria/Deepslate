package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.menuController
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class HomeMenu(title: Component, opener: HumanEntity, previousMenu: BaseMenu?) :
	BaseMenu(title, opener, previousMenu, size = 54) {

	init {
		fillMaterial = Material.WHITE_STAINED_GLASS_PANE
	}

	class Factory: BaseMenu.Factory(Component.text("Accueil", NamedTextColor.GOLD, TextDecoration.BOLD), Material.GRASS_BLOCK) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?): BaseMenu = HomeMenu(title, opener, previousMenu)
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