package fr.pickaria.market

import fr.pickaria.shared.models.Order
import fr.pickaria.shared.models.OrderType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


internal class CreateBuyOrderCommand : CommandExecutor, TabCompleter {
	companion object {
		val MATERIALS = Material.values().map { it.name.lowercase() }
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			if (args.isEmpty()) {
				sender.sendMessage(Component.text("Vous devez entrer l'objet que vous souhaiter acheter.", NamedTextColor.RED))
				return true
			}

			val material: Material = args.getOrNull(0)?.let {
				Material.getMaterial(it.uppercase())
			} ?: run {
				sender.sendMessage(Component.text("Ce matériaux n'est pas achetable.", NamedTextColor.RED))
				return false
			}


			val maxPrice = args.getOrNull(1)?.let {
				try {
					it.toDouble()
				} catch (_: NumberFormatException) {
					val message = Component.text("Le prix que vous avez entré est incorrect.", NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}
			} ?: run {
				val (maxPrice, _, _) = Order.getPrices(material)
				maxPrice
			}

			if (maxPrice < 1.0) {
				sender.sendMessage(Component.text("Le prix doit être supérieur à 1.0.", NamedTextColor.RED))
				return false
			}

			val quantity = args.getOrNull(2)?.let {
				try {
					it.toInt()
				} catch (_: NumberFormatException) {
					val message = Component.text("La quantité que vous avez entré est incorrecte.", NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}
			} ?: 1

			if (quantity <= 0) {
				val message = Component.text("La quantité que vous avez entré est incorrecte.", NamedTextColor.RED)
				sender.sendMessage(message)
				return true
			}

			val order = Order.create(sender, material, OrderType.BUY, quantity, maxPrice)

			if (order != null) {
				val message = Component.text("Ordre d'achat n°", NamedTextColor.GRAY)
					.append(Component.text(order.id, NamedTextColor.GOLD))
					.append(Component.text(" créé.", NamedTextColor.GRAY))

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
					MATERIALS.filter { it.startsWith(args[0]) }
				}

				2 -> {
					Material.getMaterial(args[0].uppercase())?.let { material ->
						Order.getPrices(material).toList().map { it.toString() }
					} ?: listOf()
				}

				3 -> {
					listOf("1", "16", "32", "64")
				}

				else -> listOf()
			}
		}

		return listOf()
	}
}