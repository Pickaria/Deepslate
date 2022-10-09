package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuItem
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class HomeMenu(title: String, opener: HumanEntity?, previousMenu: BaseMenu?, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	override fun initMenu() {
		super.setMenuItem(0, 1, MenuItem(Material.OAK_SIGN, "Towny", "§7Menus relatifs aux villes."))

		super.setMenuItem(2,
			1,
			MenuItem(
				Material.OAK_FENCE_GATE,
				"Villes",
				"§7Liste des villes du serveur.",
				"§7Vous permet de vous téléporter à chaque ville.",
				"Clic-gauche pour ouvrir le sous-menu"
			)
				.setCallback { event ->
					event.whoClicked.sendMessage("Hello")
				})

		super.setMenuItem(3,
			1,
			MenuItem(
				Material.OAK_FENCE_GATE,
				"Test menu"
			)
				.setCallback { event ->
					event.whoClicked.sendMessage("Jobs")
				})
	}
}