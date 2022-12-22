package fr.pickaria.shops

import fr.pickaria.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe

class CreateShops : CommandExecutor, TabCompleter {
	private enum class VillagerType { ARTEFACTS, BANK, POTIONS, REWARDS, MARKET }

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val key = VillagerType.valueOf(args.first().uppercase())
			val offers: List<MerchantRecipe> = when (key) {
				VillagerType.ARTEFACTS -> getArtefactsOffers()
				VillagerType.BANK -> getBankOffers()
				VillagerType.POTIONS -> getPotionsOffers()
				VillagerType.REWARDS -> getRewardsOffers()
				VillagerType.MARKET -> TODO()
			}

			Config.villagers[key.name.lowercase()]?.create(sender.location, offers)
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>
	): MutableList<String> =
		VillagerType.values().map { it.name.lowercase() }.filter { it.startsWith(args.first()) }.toMutableList()
}