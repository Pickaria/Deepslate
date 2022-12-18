package fr.pickaria

import fr.pickaria.economy.Credit
import fr.pickaria.economy.Key
import fr.pickaria.reforge.getAttributeItem
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

			villager.profession = Villager.Profession.LIBRARIAN
			villager.villagerType = Villager.Type.TAIGA

			val merchant = villager as Merchant
			val attributeItem = getAttributeItem()

			merchant.recipes = listOf(
				MerchantRecipe(Key.item(), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(8, 128.0))
				},
				MerchantRecipe(Key.item(2), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(16, 128.0))
				},
				MerchantRecipe(Key.item(4), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(32, 128.0))
				},
				MerchantRecipe(Key.item(8), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(64, 128.0))
				},

				MerchantRecipe(attributeItem.asQuantity(3), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(1, 128.0))
				},
				MerchantRecipe(attributeItem.asQuantity(6), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(6, 128.0))
				},
				MerchantRecipe(attributeItem.asQuantity(9), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(9, 128.0))
				},
				MerchantRecipe(attributeItem.asQuantity(64), Int.MAX_VALUE).apply {
					uses = 0
					addIngredient(Credit.item(64, 128.0))
				},
			)

			villager.customName(Component.text("Michel"))
			villager.isCustomNameVisible = true
		}

		return true
	}
}
