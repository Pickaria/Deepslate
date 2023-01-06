package fr.pickaria.vue.market

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.market.giveItems
import fr.pickaria.model.market.Order
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

@CommandAlias("cancel")
@CommandPermission("pickaria.commands.cancel")
@Description("Ouvre le menu ou annule une vente.")
class CancelOrderCommand : BaseCommand() {
	companion object {
		fun setupContext(
			commandContexts: CommandContexts<BukkitCommandExecutionContext>,
			commandCompletions: CommandCompletions<BukkitCommandCompletionContext>
		) {
			commandContexts.registerContext(Order::class.java) {
				val arg: String = it.popFirstArg()

				try {
					Order.get(arg.toInt())
				} catch (_: IllegalArgumentException) {
					throw InvalidCommandArgument("Order '$arg' not found.")
				}
			}

			commandCompletions.registerCompletion("ownorders") { context ->
				Order.get(context.player).map { it.id.toString() }
			}
		}
	}

	@Default
	@CommandCompletion("@ownorders")
	fun onDefault(sender: Player, @Optional order: Order?) {
		if (order == null) {
			throw InvalidCommandArgument("Cette vente ne peut pas être annulée.")
		}

		if (order.seller != sender) {
			throw InvalidCommandArgument("Vous n'êtes pas autorisé à annuler cet vente.")
		}

		if (order.delete() >= 1) {
			giveItems(sender, order.material, order.amount)
			val message = Component.text("Ordre supprimé avec succès.", NamedTextColor.GRAY)
			sender.sendMessage(message)
		} else {
			throw InvalidCommandArgument("Une erreur est survenue lors de l'annulation de la vente.")
		}
	}
}