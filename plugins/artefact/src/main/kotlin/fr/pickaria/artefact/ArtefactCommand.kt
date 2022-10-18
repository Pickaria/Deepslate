package fr.pickaria.artefact

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener

internal class ArtefactCommand : CommandExecutor, Listener, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			args?.getOrNull(0)?.let {
				Artefact.valueOf(it.uppercase()).getConfig()
			}?.let { artefact ->
				sender.inventory.addItem(createArtefactReceptacle(artefact))
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>?
	): MutableList<String> {
		return Artefact.values().map { it.name.lowercase() }.toMutableList()
	}
}