package fr.pickaria.shard

import fr.pickaria.economy.Credit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType

internal class PlaceShopCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val villager = sender.location.world.spawnEntity(sender.location, EntityType.VILLAGER) as Villager
			villager.setAI(false)
			villager.isSilent = true

			val recipe = MerchantRecipe(ItemStack(Material.GRASS_BLOCK), Int.MAX_VALUE)
			recipe.addIngredient(Shard.item(3))
			recipe.addIngredient(Credit.item(50))

			villager.persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)

			villager.recipes = listOf(recipe)
		}

		return true
	}
}
