package fr.pickaria.shard

import fr.pickaria.economy.Credit
import fr.pickaria.economy.Shard
import fr.pickaria.spawner.spawnVillager
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe
import fr.pickaria.Config as ArtefactConfig

internal class PlaceShopCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val villager = spawnVillager(sender.location, "Shard")

			villager.profession = Villager.Profession.CLERIC
			villager.villagerType = Villager.Type.SWAMP

			val merchant = villager as Merchant
			val artefacts = ArtefactConfig.lazyArtefacts

			merchant.recipes = artefacts.map { (_, artefact) ->
				val item = artefact.createReceptacle()
				val price: Int = artefact.value

				MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Shard.item(price))
					addIngredient(Credit.item(price * 4))
				}
			}

			villager.customName(Component.text("Bertrand"))
			villager.isCustomNameVisible = true
		}

		return true
	}
}
