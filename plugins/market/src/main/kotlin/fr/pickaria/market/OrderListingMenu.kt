package fr.pickaria.market

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import fr.pickaria.shared.models.SellOrder
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

		SellOrder.get().forEach {
			val material = it.item.type

			setMenuItem {
				this.x = x++
				this.y = 0
				this.material = material
				component = Component.translatable(material.translationKey())
				lore = MenuLore.build {
					keyValues = mapOf(
						"Quantité en vente" to it.amount,
						"Capitalisation boursière" to economy.format(it.amount * it.averagePrice),
						"Achat immédiat" to economy.format(it.maximumPrice),
						"Vente immédiate" to economy.format(it.minimumPrice),
						"Prix moyen" to economy.format(it.averagePrice),
					)
					leftClick = "Clic-gauche pour voir les options d'achat"
					rightClick = "Clic-gauche pour voir les options de vente"
				}
			}
		}
	}
}