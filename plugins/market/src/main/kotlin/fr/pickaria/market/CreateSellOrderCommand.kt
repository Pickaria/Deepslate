package fr.pickaria.market

import fr.pickaria.shared.models.SellOrder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


internal class CreateSellOrderCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			if (args.isEmpty()) {
				sender.sendMessage(Component.text("Vous devez entrer un prix unitaire.", NamedTextColor.RED))
				return true
			}


			val item = sender.inventory.itemInMainHand

			val price = args.getOrNull(0)?.let {
				try {
					it.toDouble()
				} catch (_: NumberFormatException) {
					val message = Component.text("Le prix que vous avez entré est incorrect.",NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}
			} ?: run {
				sender.sendMessage(Component.text("Vous devez entrer un prix unitaire.", NamedTextColor.RED))
				return false
			}

			val quantity = args.getOrNull(1)?.let {
				try {
					it.toInt()
				} catch (_: NumberFormatException) {
					val message = Component.text("La quantité que vous avez entré est incorrecte.",NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}
			} ?: item.amount

			if (quantity <= 0) {
				val message = Component.text("La quantité que vous avez entré est incorrecte.",NamedTextColor.RED)
				sender.sendMessage(message)
				return true
			}

			if (!sender.inventory.contains(item, quantity)) {
				val message = Component.text("Vous n'avez pas autant de ")
					.append(Component.translatable(item.type.translationKey()))
					.append(Component.text(" dans votre inventaire."))
					.color(NamedTextColor.RED)
				sender.sendMessage(message)

				return true
			}

			val order = SellOrder.create(sender, item.asQuantity(quantity), price)

			order?.let {
				sender.inventory.remove(item)

				val message = Component.text("Ordre de vente n°",NamedTextColor.GRAY)
					.append(Component.text(it.id, NamedTextColor.GOLD))
					.append(Component.text(" créé.", NamedTextColor.GRAY))

				sender.sendMessage(message)
			}
		}

		return true
	}
}