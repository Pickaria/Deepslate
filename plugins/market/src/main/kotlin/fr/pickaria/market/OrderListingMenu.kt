package fr.pickaria.market

import fr.pickaria.market.menu.BuyMenu
import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import fr.pickaria.menu.menuController
import fr.pickaria.shared.models.Order
import fr.pickaria.shared.models.OrderType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

internal class OrderListingMenu(title: Component, opener: HumanEntity, previousMenu: BaseMenu? = null) :
	BaseMenu(title, opener, previousMenu, size = 54) {

	internal class Factory(material: Material = Material.EMERALD) :
		BaseMenu.Factory(Component.text("Marché", NamedTextColor.GOLD, TextDecoration.BOLD), material) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?): BaseMenu =
			OrderListingMenu(title, opener, previousMenu)
	}

	override fun initMenu() {
		var x = 0

		Order.get(OrderType.SELL).forEach { order ->
			setMenuItem {
				this.x = x++
				this.y = 0
				material = order.material
				lore = MenuLore.build {
					keyValues = mapOf(
						"Quantité en vente" to order.amount,
						"Capitalisation boursière" to economy.format(order.amount * order.averagePrice),
						"Achat immédiat" to economy.format(order.maximumPrice),
						"Vente immédiate" to economy.format(order.minimumPrice),
						"Prix moyen" to economy.format(order.averagePrice),
					)
					leftClick = "Clic-gauche pour voir les options d'achat"
					rightClick = "Clic-gauche pour voir les options de vente"
				}
				name = Component.translatable(order.material.translationKey())
				leftClick = {
					val title = Component.text("Acheter ", NamedTextColor.GRAY)
						.append(Component.translatable(order.material.translationKey(), NamedTextColor.GOLD))
						.decorate(TextDecoration.BOLD)
					val menu = BuyMenu(order.material, title, opener, this@OrderListingMenu)
					menuController.openMenu(opener, menu)
				}
			}
		}
	}
}