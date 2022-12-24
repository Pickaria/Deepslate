package fr.pickaria.vue.market

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.market.getPrices
import fr.pickaria.model.market.Order
import fr.pickaria.model.market.OrderType
import fr.pickaria.model.market.marketConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat
import kotlin.math.min

@CommandAlias("sell")
@CommandPermission("pickaria.command.sell")
@Description("Met en vente un objet.")
class SellCommand : BaseCommand() {
	companion object {
		private val formatter = DecimalFormat("#.##")

		fun setupContext(commandCompletions: CommandCompletions<BukkitCommandCompletionContext>) {
			commandCompletions.registerCompletion("inventory") { context ->
				context.player.inventory.contents.filterNotNull().map { it.type.name.lowercase() }.toSet()
			}

			commandCompletions.registerCompletion("sellcount") { context ->
				val count = try {
					val material = context.getContextValue(Material::class.java)
					context.player.inventory.filter { it?.type == material }.sumOf { it.amount }
				} catch (_: InvalidCommandArgument) {
					0
				}

				listOf(1, 16, 32, 64, count).filter { it <= count }.map { it.toString() }
			}

			commandCompletions.registerCompletion("sellprices") { context ->
				try {
					val material = context.getContextValue(Material::class.java)
					Order.getPrices(material).toList().map { formatter.format(it) }
				} catch (_: InvalidCommandArgument) {
					emptyList<String>()
				}
			}
		}
	}

	private fun quickSell(sender: Player) {
		val item = sender.inventory.itemInMainHand
		val material = item.type

		if (material.isAir || item.hasItemMeta()) {
			throw ConditionFailedException("Cet objet ne peut pas être vendu.")
		}

		val quantity = sender.inventory.filter { it?.type == material }.sumOf { it.amount }

		if (quantity <= 0) {
			throw ConditionFailedException("La quantité que vous avez entré est incorrecte.")
		}

		val isSelling = Order.get(material, OrderType.SELL).isNotEmpty()

		val price = if (isSelling) {
			getPrices(material).first
		} else {
			throw ConditionFailedException("Vous devez entrer un prix unitaire pour vendre cet objet.")
		}

		if (price < marketConfig.minimumPrice) {
			throw ConditionFailedException("Le prix doit être supérieur ou égal à ${marketConfig.minimumPrice}.")
		}

		createOrder(sender, material, quantity, price)
	}

	private fun createOrder(sender: Player, material: Material, quantity: Int, price: Double) {
		val order = Order.create(sender, material, OrderType.SELL, quantity, price)
		val item = ItemStack(material)

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
		}
	}

	@Default
	@Syntax("<material> <quantity> <sell price>")
	@CommandCompletion("@inventory @sellcount @sellprices")
	fun onSell(
		sender: Player,
		@Optional material: Material?,
		@Conditions("limits:min=1") @Optional quantity: Int?,
		@Optional sellPrice: Double?
	) {
		val isQuickSell = material == null && quantity == null && sellPrice == null
		if (isQuickSell) {
			quickSell(sender)
			return
		}

		if (material!!.isAir) {
			throw ConditionFailedException("Cet objet ne peut pas être vendu.")
		}

		val quantity = quantity ?: material.maxStackSize

		if (quantity <= 0) {
			throw ConditionFailedException("La quantité que vous avez entré est incorrecte.")
		}

		val price: Double = sellPrice ?: run {
			val isSelling = Order.get(material, OrderType.SELL).isNotEmpty()

			if (isSelling) {
				getPrices(material).first
			} else {
				throw ConditionFailedException("Vous devez entrer un prix unitaire pour vendre cet objet.")
			}
		}

		if (price < marketConfig.minimumPrice) {
			throw ConditionFailedException("Le prix doit être supérieur ou égal à ${marketConfig.minimumPrice}.")
		}

		val item = ItemStack(material)

		if (!sender.inventory.containsAtLeast(item, quantity)) {
			val message = Component.text("Vous n'avez pas autant de ")
				.append(Component.translatable(material.translationKey()))
				.append(Component.text(" dans votre inventaire."))
				.color(NamedTextColor.RED)
			sender.sendMessage(message)

			return
		}

		createOrder(sender, material, quantity, price)
	}
}