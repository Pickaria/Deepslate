package fr.pickaria.reward

import fr.pickaria.database.models.PlayerReward
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

internal class CollectCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val today = LocalDate.now()
			val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
				.withLocale(Locale.FRENCH)

			PlayerReward.get(sender.uniqueId)?.apply {
				lastReward = today
				rewardStreak = (rewardStreak + 1) % Config.maximumStreak
			} ?: PlayerReward.create(sender.uniqueId, 1, today)
			sender.sendMessage(Component.text("Collected for ${today.format(formatter)}."))
		}

		return true
	}
}