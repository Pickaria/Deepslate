package fr.pickaria.market.menu

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.market.economy
import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class OwnOrdersMenu(title: Component, opener: HumanEntity, previousMenu: BaseMenu? = null) :
	BaseMenu(title, opener, previousMenu, size = 54) {

	class Factory : BaseMenu.Factory(Component.text("Mes ordres"), Material.PAPER, listOf()) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?): BaseMenu =
			OwnOrdersMenu(title, opener, previousMenu)
	}

	override fun initMenu() {
		var x = 0

		Order.get(opener as OfflinePlayer).forEach { order ->
			val orderType = if (order.type == OrderType.SELL) {
				"vendre"
			} else {
				"achat"
			}

			setMenuItem {
				this.x = x++
				this.y = 0
				material = order.material
				lore = MenuLore.build {
					keyValues = mapOf(
						"Identifiant" to order.id,
						"Type d'ordre" to orderType,
						"Quantité en vente" to order.amount,
						"Prix de vente" to economy.format(order.price),
					)
					leftClick = "Clic-gauche pour retirer de la vente"
				}
				name = Component.translatable(order.material.translationKey())
				leftClick = {
					(it.whoClicked as Player).chat("/cancel ${order.id}")
					updateMenu()
				}
			}
		}
	}
}