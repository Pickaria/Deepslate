package fr.pickaria.shard

import fr.pickaria.artefact.createArtefactReceptacle
import fr.pickaria.artefact.getArtefactConfig
import fr.pickaria.shopapi.spawnVillager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe
import kotlin.math.floor

internal class PlaceShopCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val villager = spawnVillager(sender.location, "test")

			villager.profession = Villager.Profession.ARMORER
			villager.villagerType = Villager.Type.PLAINS

			val merchant = villager as Merchant

			val money = getShardBalance(sender)

			val artefacts = artefactConfig?.artefacts ?: mapOf()

			merchant.recipes = artefacts.map { (_, config) ->
				val item = createArtefactReceptacle(config)
				val price: Int = getArtefactConfig(item)?.value ?: (floor(Math.random() * 64) + 1).toInt()

				// Maximum the player can buy
				val canBuy = money / price

				MerchantRecipe(item.clone().apply { amount = 1 }, canBuy).apply {
					uses = 0
					addIngredient(Shard.item(price))
				}
			}
		}

		return true
	}
}
