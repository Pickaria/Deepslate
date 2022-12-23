package fr.pickaria.vue.shop

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.model.shop.ShopType
import fr.pickaria.model.shop.shopConfig
import fr.pickaria.model.shop.toController
import org.bukkit.entity.Player


@CommandAlias("placeshop|createshop")
@CommandPermission("pickaria.admin.command.placeshop")
class PlaceShop : BaseCommand() {
	companion object {
		fun setupContext(
			commandContexts: CommandContexts<BukkitCommandExecutionContext>,
			commandCompletions: CommandCompletions<BukkitCommandCompletionContext>
		) {
			commandContexts.registerContext(ShopType::class.java) {
				val arg: String = it.popFirstArg()

				try {
					ShopType.valueOf(arg.uppercase())
				} catch (_: IllegalArgumentException) {
					throw InvalidCommandArgument("Shop of type '$arg' does not exists.")
				}
			}

			commandCompletions.registerCompletion("shoptype") {
				ShopType.values().map { it.name.lowercase() }
			}
		}
	}

	@Default
	@Syntax("[shop type]")
	@CommandCompletion("@shoptype")
	fun onCommand(sender: Player, @Optional type: ShopType?) {
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