package fr.pickaria.reward

import fr.pickaria.economy.Key
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

internal class PlaceShopCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val villager = spawnVillager(sender.location, "Shard")

			villager.profession = Villager.Profession.SHEPHERD
			villager.villagerType = Villager.Type.PLAINS

			val merchant = villager as Merchant

			merchant.recipes = Config.rewards.filter { it.value.purchasable }.map { (key, config) ->
				createReward(key)?.let { item ->
					MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
						uses = 0
						addIngredient(Key.item(config.keys))
						addIngredient(Shard.item(config.shards))
					}
				}
			}

			villager.customName(Component.text("George"))
			villager.isCustomNameVisible = true
		}

		return true
	}
}
