package fr.pickaria.vue.menu

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import fr.pickaria.DEFAULT_MENU
import fr.pickaria.controller.home.homeEntries
import fr.pickaria.menu.open
import org.bukkit.entity.Player

@CommandAlias("menu")
class MenuCommand(commandManager: PaperCommandManager) : BaseCommand() {
	init {
		commandManager.commandCompletions.registerCompletion("menus") { _ ->
			homeEntries.map { it.entry.key }
		}
	}

	@Default
	@CommandCompletion("@menus")
	fun onDefault(player: Player, @Optional subMenu: String?) {
		player.open(subMenu ?: DEFAULT_MENU)
	}
}
