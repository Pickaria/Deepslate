package fr.pickaria.vue.market

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import fr.pickaria.controller.market.buy
import fr.pickaria.controller.market.overflowStacks
import fr.pickaria.model.market.Order
import fr.pickaria.shared.give
import org.bukkit.Material
import org.bukkit.entity.Player

@CommandAlias("buy")
@CommandPermission("pickaria.command.buy")
@Description("Achète une quantité de matériaux.")
class BuyCommand(manager: PaperCommandManager) : BaseCommand() {
	init {
		manager.commandConditions.addCondition(
			Material::class.java,
			"must_be_selling"
		) { _, _, material ->
			val isNotSelling = Order.get(material).isEmpty()

			if (isNotSelling) {
				throw ConditionFailedException("Ce matériau n'est pas en stocks.")
			}
		}

		manager.commandCompletions.registerCompletion("selling_material") { context ->
			Order.getMaterials().map { it.name.lowercase() }.filter { it.startsWith(context.input) }
		}

		manager.commandCompletions.registerCompletion("buy_amount") { context ->
			val count = Order.get(context.getContextValue(Material::class.java)).sumOf { it.amount }
			listOf(1, 16, 32, 64, count).filter { it <= count }.map { it.toString() }
		}
	}

	@Default
	@CommandCompletion("@selling_material @buy_amount")
	fun onDefault(
		sender: Player,
		@Conditions("must_be_selling") material: Material,
		@Default("1") @Conditions("limits:min=1") quantity: Int
	) {
		val boughtAmount = buy(sender, material, quantity)
		overflowStacks(material, boughtAmount).forEach {
			sender.give(it)
		}
	}
}