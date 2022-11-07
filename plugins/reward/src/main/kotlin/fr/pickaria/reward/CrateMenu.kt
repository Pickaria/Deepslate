package fr.pickaria.reward

import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

internal class CrateMenu(title: Component, opener: HumanEntity, previousMenu: BaseMenu? = null) :
	BaseMenu(title, opener, previousMenu, size = 54) {

	internal class Factory :
		BaseMenu.Factory(Component.text("Récompenses"), Material.SHULKER_BOX, listOf()) {

		override fun create(opener: HumanEntity, previousMenu: BaseMenu?): BaseMenu =
			CrateMenu(title, opener, previousMenu)
	}

	override fun initMenu() {
		var x = 0
		Config.rewards.forEach { (key, reward) ->
			setMenuItem {
				this.x = x++
				name = reward.name
				material = reward.material
				lore = MenuLore.build {
					keyValues = mapOf("Prix" to economy.format(reward.price))
				}
				leftClick = {
					(it.whoClicked as Player).chat("/crate $key")
				}
			}
		}
	}
}