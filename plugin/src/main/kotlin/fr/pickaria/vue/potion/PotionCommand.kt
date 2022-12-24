package fr.pickaria.vue.potion

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.model.potion.PotionType
import fr.pickaria.model.potion.potionConfig
import fr.pickaria.model.potion.toController
import fr.pickaria.shared.give
import org.bukkit.entity.Player

@CommandAlias("givepotion")
@CommandPermission("pickaria.admin.command.givepotion")
class PotionCommand : BaseCommand() {
	companion object {
		fun setupContext(
			commandContexts: CommandContexts<BukkitCommandExecutionContext>,
			commandCompletions: CommandCompletions<BukkitCommandCompletionContext>
		) {
			commandContexts.registerContext(PotionType::class.java) {
				val arg = it.popFirstArg()

				try {
					PotionType.valueOf(arg.uppercase())
				} catch (_: IllegalArgumentException) {
					throw InvalidCommandArgument("Potion of type '$arg' does not exists.")
				}
			}

			commandCompletions.registerCompletion("potiontype") {
				PotionType.values().map { it.name.lowercase() }
			}
		}
	}

	@Default
	@Syntax("<potion type> [amount]")
	@CommandCompletion("@potiontype")
	fun onCommand(sender: Player, potionType: PotionType, @Default("1") amount: Int) {
		val itemStack = potionConfig.potions[potionType.name.lowercase()]?.toController()?.create(amount)
			?: throw InvalidCommandArgument("Potion item could not be created.")
		sender.give(itemStack)
	}
}