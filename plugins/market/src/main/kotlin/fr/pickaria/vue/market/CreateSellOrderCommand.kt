package fr.pickaria.vue.market

import fr.pickaria.controller.market.getPrices
import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.model.market.Config
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.min


/*
 * /sell [material = current in hand] [quantity = stack in hand's size or max stack size] [minimum sell price = market price or error]
 *
 * Creates a sell order for the item in the main hand of the player.
 * If no price is specified, the minimum price of the current listings will be used.
 *  -> If the material has no listing, the player will be asked to enter a price.
 * If no quantity is specified, the size of the stack in hand will be used.
 */
internal class CreateSellOrderCommand : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val quickSell = args.isEmpty()

			val material: Material = if (quickSell) {
				val item = sender.inventory.itemInMainHand
				val material = item.type

				if (material.isAir || item.hasItemMeta()) {
					val message = Component.text("Cet objet ne peut pas être vendu.", NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}

				material
			} else {
				Material.getMaterial(args[0].uppercase()) ?: run {
					val message = Component.text("Le matériau que vous avez entré est incorrect.", NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}
			}

			val quantity: Int = if (quickSell) {
				sender.inventory.filter { it?.type == material }.sumOf { it.amount }
			} else {
				args.getOrNull(1)?.let {
					try {
						it.toInt()
					} catch (_: NumberFormatException) {
						val message =
							Component.text("La quantité que vous avez entré est incorrecte.", NamedTextColor.RED)
						sender.sendMessage(message)
						return true
					}
				} ?: material.maxStackSize
			}

			if (quantity <= 0) {
				val message = Component.text("La quantité que vous avez entré est incorrecte.", NamedTextColor.RED)
				sender.sendMessage(message)
				return true
			}

			val price: Double = args.getOrNull(2)?.let {
				try {
					it.toDouble()
				} catch (_: NumberFormatException) {
					val message = Component.text("Le prix que vous avez entré est incorrect.", NamedTextColor.RED)
					sender.sendMessage(message)
					return true
				}
			} ?: run {
				val isSelling = Order.get(material, OrderType.SELL).isNotEmpty()

				if (isSelling) {
					getPrices(material).first
				} else {
					sender.sendMessage(
						Component.text(
							"Vous devez entrer un prix unitaire pour vendre cet objet.",
							NamedTextColor.RED
						)
					)
					return false
				}
			}

			if (price < Config.minimumPrice) {
				sender.sendMessage(
					Component.text(
						"Le prix doit être supérieur ou égal à ${Config.minimumPrice}.",
						NamedTextColor.RED
					)
				)
				return false
			}

			val item = ItemStack(material)

			if (!sender.inventory.containsAtLeast(item, quantity)) {
				val message = Component.text("Vous n'avez pas autant de ")
					.append(Component.translatable(material.translationKey()))
					.append(Component.text(" dans votre inventaire."))
					.color(NamedTextColor.RED)
				sender.sendMessage(message)

				return true
			}

			val order = Order.create(sender, material, OrderType.SELL, quantity, price)

			if (order != null) {
				var removedAmount = 0
				do {
					val amountToRemove = min(quantity - removedAmount, material.maxStackSize)
					sender.inventory.removeItem(item.asQuantity(amountToRemove))
					removedAmount += amountToRemove
				} while (removedAmount < quantity)

				val message = Component.text("Ordre de vente n°", NamedTextColor.GRAY)
					.append(Component.text(order.id, NamedTextColor.GOLD))
					.append(Component.text(" placé.", NamedTextColor.GRAY))

				sender.sendMessage(message)

				// TODO: Check if sell order can sell immediately
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
		val material = Material.getMaterial(args[0].uppercase())

		if (sender is Player) {
			return when (args.size) {
				1 -> {
					sender.inventory.contents.filterNotNull().map { it.type.name.lowercase() }.toSet().toList()
				}

				2 -> {
					val count: Int = material?.let {
						sender.inventory.filter { it?.type == material }.sumOf { it.amount }
					} ?: 0
					listOf("1", "16", "32", "64", count.toString())
				}

				3 -> {
					material?.let {
						Order.getPrices(material).toList().map { it.toString() }
					} ?: listOf()
				}

				else -> listOf()
			}
		}

		return listOf()
	}
}