package fr.pickaria.vue.miniblocks

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.toController
import fr.pickaria.menu.open
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.miniblocks.MiniBlock
import fr.pickaria.model.miniblocks.miniBlocksConfig
import fr.pickaria.shared.MiniMessage
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

@CommandAlias("miniblock")
@CommandPermission("pickaria.command.miniblocks")
class MiniBlockCommand : BaseCommand() {
	companion object {
		fun setupContext(manager: PaperCommandManager) {
			manager.commandContexts.registerContext(MiniBlock::class.java) { context ->
				Material.getMaterial(context.popFirstArg().uppercase())?.let { material ->
					miniBlocksConfig.miniBlocks.find {
						it.material == material
					}
				} ?: throw InvalidCommandArgument("Mini bloc non trouvé.")
			}

			manager.commandCompletions.registerCompletion("miniblocks") {
				miniBlocksConfig.miniBlocks.map { it.material.name.lowercase() }
			}
		}
	}

	@HelpCommand
	@Syntax("")
	fun doHelp(help: CommandHelp) {
		help.showHelp()
	}

	@Default
	@Subcommand("menu")
	fun onDefault(player: Player) {
		player open "miniblocks"
	}

	@Subcommand("buy")
	@CommandCompletion("@miniblocks")
	@Description("Achète des mini blocs.")
	fun onBuy(player: Player, miniBlock: MiniBlock, @Default("1") amount: Int) {
		val unitPrice = miniBlock.price ?: miniBlocksConfig.defaultPrice
		val totalPrice = unitPrice * amount

		if (!player.has(Credit, totalPrice)) {
			throw ConditionFailedException("Vous n'avez pas assez d'argent pour acheter $amount mini blocs de ce type.")
		}

		if (player.withdraw(Credit, totalPrice).transactionSuccess()) {
			player.give(miniBlock.toController().create(amount))
			val message = if (amount > 1) miniBlocksConfig.buyMessagePlural else miniBlocksConfig.buyMessageSingular
			MiniMessage(message) {
				"amount" to amount
				"material" to Component.translatable(miniBlock.material.translationKey())
			}.send(player)
		}
	}
}