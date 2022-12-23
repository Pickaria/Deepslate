package fr.pickaria.vue.market

import fr.pickaria.controller.market.giveItems
import fr.pickaria.model.market.Order
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


/*
 * /cancel <order id>
 *
 * Cancels an order.
 */
internal class CancelOrderCommand : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val orderId = args.getOrNull(0)?.let {
				try {
					it.toInt()
				} catch (_: NumberFormatException) {
					val message = Component.text(
						"L'identifiant de l'ordre que vous avez entré est incorrect.",
						NamedTextColor.RED
					)
					sender.sendMessage(message)
					return true
				}
			} ?: run {
				sender.sendMessage(Component.text("Vous devez entrer un identifiant.", NamedTextColor.RED))
				return false
			}

			// Try to delete order
			Order.get(sender, orderId)?.let {
				it.delete()
				giveItems(sender, it.material, it.amount)
				val message = Component.text("Ordre supprimé avec succès.", NamedTextColor.GRAY)
				sender.sendMessage(message)
			} ?: run {
				val message = Component.text("Cet ordre n'existe pas.", NamedTextColor.RED)
				sender.sendMessage(message)
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>
	): List<String> {
		if (sender is Player) {
			return when (args.size) {
				1 -> {
					Order.get(sender).map { it.id.toString() }
				}

				else -> listOf()
			}
		}

		return listOf()
	}
}