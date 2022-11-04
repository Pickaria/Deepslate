package fr.pickaria.menu.sub

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.menuController
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import fr.pickaria.shard.createShardItem
import fr.pickaria.shard.getShardBalance
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta

class HomeMenu(title: Component, opener: HumanEntity, previousMenu: BaseMenu?) :
	BaseMenu(title, opener, previousMenu, size = 54) {

	init {
		fillMaterial = Material.WHITE_STAINED_GLASS_PANE
	}

	class Factory: BaseMenu.Factory(Component.text("Accueil", NamedTextColor.GOLD, TextDecoration.BOLD), Material.GRASS_BLOCK) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?): BaseMenu = HomeMenu(title, opener, previousMenu)
	}

	override fun initMenu() {
		setMenuItem {
			this.x = 8
			this.y = 0
			material = Material.BUNDLE
			name = Component.text("Sacoche de Pickarite", NamedTextColor.GOLD)
		}.itemStack?.let {
			val bundle = it.itemMeta as BundleMeta

			var balance = getShardBalance(opener as OfflinePlayer)
			val items = mutableListOf<ItemStack>()

			for (i in 0 until balance step 64) {
				val amount = if (balance > 64) 64 else balance
				items.add(createShardItem(amount))
				balance -= 64
			}

			bundle.setItems(items)
			it.itemMeta = bundle
		}

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