package fr.pickaria.market.menu

import fr.pickaria.market.economy
import fr.pickaria.market.getPrices
import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

internal class SellMenu(private val material: Material, title: Component, opener: HumanEntity, previousMenu: BaseMenu) :
	BaseMenu(title, opener, previousMenu, size = 36) {

	init {
		fillMaterial = Material.ORANGE_STAINED_GLASS_PANE
	}

	override fun initMenu() {
		val price = getPrices(material).first
		val unitPrice = economy.format(price)

		listOf(1 to 1, 16 to 3, 32 to 5, 64 to 7).forEach { (amount, x) ->
			val offerPrice = economy.format(price * amount)

			setMenuItem {
				this.x = x
				y = 1
				name = Component.text("$amount ")
					.append(Component.translatable(this@SellMenu.material.translationKey()))

				// TODO: Check if player has enough in inventory
					this.material = this@SellMenu.material
					lore = MenuLore.build {
						leftClick = "Clic-gauche pour vendre $amount"
						keyValues = mapOf("Prix unitaire" to unitPrice, "Prix total" to offerPrice)
					}
					leftClick = {
						(it.whoClicked as Player).chat("/sell ${material.name.lowercase()} $amount $price")
						updateMenu()
					}

				this.amount = amount
			}
		}
	}
}