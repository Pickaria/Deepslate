package fr.pickaria.market

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import fr.pickaria.shared.models.Order
import fr.pickaria.shared.models.OrderType
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

internal class OrderListingMenu(title: String, opener: HumanEntity, previousMenu: BaseMenu? = null, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	internal class Factory(material: Material = Material.EMERALD) : BaseMenu.Factory("§6§lMarché", material) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?, size: Int): BaseMenu =
			OrderListingMenu(title, opener, previousMenu, size)
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
				component = Component.translatable(order.material.translationKey())
			}
		}
	}
}