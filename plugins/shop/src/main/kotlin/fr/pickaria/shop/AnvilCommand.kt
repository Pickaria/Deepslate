package fr.pickaria.shop

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class AnvilCommand(private val plugin: JavaPlugin): CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			AnvilGUI.Builder()
				.onClose { player: Player ->
					player.sendMessage("You closed the inventory.")
				}
				.onComplete { player: Player, text: String ->
					if (text.equals("you", ignoreCase = true)) {
						player.sendMessage("You have magical powers!")
						return@onComplete AnvilGUI.Response.close()
					} else {
						return@onComplete AnvilGUI.Response.text("Incorrect.")
					}
				}
				.text("") //sets the text the GUI should start with
				.title("Chercher un objet...") //set the title of the GUI (only works in 1.14+)
				.plugin(plugin) //set the plugin instance
				.open(sender) //opens the GUI for the player provided
		}

		return true
	}
}
