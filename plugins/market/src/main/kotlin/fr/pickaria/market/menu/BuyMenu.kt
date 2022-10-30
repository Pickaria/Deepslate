package fr.pickaria.market.menu

import fr.pickaria.economy.has
import fr.pickaria.market.economy
import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import fr.pickaria.shared.models.Order
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

internal class BuyMenu(private val material: Material, title: Component, opener: HumanEntity, previousMenu: BaseMenu) :
	BaseMenu(title, opener, previousMenu, size = 36) {

	init {
		fillMaterial = Material.ORANGE_STAINED_GLASS_PANE
	}

	override fun initMenu() {
		val price = Order.getMaximumPrice(material)
		val unitPrice = economy.format(price)

		listOf(1 to 1, 16 to 3, 32 to 5, 64 to 7).forEach { (amount, x) ->
			val offerPrice = economy.format(price * amount)

			val player = opener as OfflinePlayer


			setMenuItem {
				this.x = x
				y = 1
				name = Component.text("$amount ")
					.append(Component.translatable(this@BuyMenu.material.translationKey()))
				if (player has price * amount) {
					this.material = this@BuyMenu.material
					lore = MenuLore.build {
						leftClick = "Clic-gauche pour acheter $amount"
						keyValues = mapOf("Prix unitaire" to unitPrice, "Prix total" to offerPrice)
					}
					leftClick = {
						(it.whoClicked as Player).chat("/buy ${material.name.lowercase()} $amount $price")
					}
				} else {
					this.material = Material.BARRIER
					lore = MenuLore.build {
						description = listOf("Vous n'avez pas assez", "d'argent pour acheter ceci.")
					}
				}
				this.amount = amount
			}
		}
	}
}