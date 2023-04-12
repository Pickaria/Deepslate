package fr.pickaria.vue.shop

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.model.shop.ShopOffer
import fr.pickaria.model.shop.shopConfig
import fr.pickaria.model.shop.toController
import org.bukkit.entity.Player


@CommandAlias("placeshop|createshop")
@CommandPermission("pickaria.admin.command.placeshop")
class PlaceShop : BaseCommand() {
	@Default
	@CommandCompletion("@shop_type")
	fun onCommand(sender: Player, @Optional type: ShopOffer?) {
		if (type == null) {
			shopConfig.villagers.forEach {
				it.value.toController().create(sender.location)
			}
		} else {
			shopConfig.villagers[type.name.lowercase()]?.toController()?.create(sender.location)
				?: throw InvalidCommandArgument("The villager could not be created.")
		}
	}
}