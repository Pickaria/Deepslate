package fr.pickaria.market

import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.*


internal class GetValueCommand : CommandExecutor {
	companion object {
		val VALUES = mapOf(
			Material.GOLD_ORE to 2.0,
			Material.IRON_ORE to 1.0,
		)

		const val MAX_DEPTH = 1
	}

	private fun getValue(initialMaterial: Material, material: Material, depth: Int = 0): Double {
		var totalValue = 0.0

		getServer().getRecipesFor(ItemStack(material)).map { recipe ->
			Bukkit.broadcastMessage("${recipe.result.type.name}")
			when (recipe) {
				is ShapedRecipe -> {
					recipe.choiceMap.mapNotNull { (it.value as? RecipeChoice.MaterialChoice) }
						.filter { recipe.result.type == material }
						.map { it.choices }
						.flatten()
				}

				is ShapelessRecipe -> {
					recipe.choiceList
						.filter { recipe.result.type == material }
						.flatMap {
							(it as RecipeChoice.MaterialChoice).choices
						}
				}

				is CookingRecipe<*> -> {
					if (recipe.result.type == material) {
						(recipe.inputChoice as RecipeChoice.MaterialChoice).choices.filterNotNull()
					} else {
						listOf()
					}
				}

				else -> {
					listOf<Material>()
				}
			}
				.filter { it != initialMaterial }
				.forEach {
					totalValue += VALUES[it] ?: if (depth < MAX_DEPTH) {
						getValue(initialMaterial, it, depth + 1)
					} else {
						0.0
					}
				}
		}

		return totalValue
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val material = sender.inventory.itemInMainHand.type

			val value = getValue(material, material)

			sender.sendMessage("Value: $value")

		}

		return true
	}
}