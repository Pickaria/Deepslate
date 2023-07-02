package fr.pickaria.controller.libraries.acf

import co.aikar.commands.BaseCommand
import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

fun JavaPlugin.initCommandManager(): PaperCommandManager {
	val manager = PaperCommandManager(this)

	manager.enableUnstableAPI("brigadier");
	manager.enableUnstableAPI("help")

	manager.locales.defaultLocale = Locale.FRENCH

	manager.setFormat(MessageType.ERROR, ChatColor.RED, ChatColor.GOLD, ChatColor.RED)
	manager.setFormat(MessageType.HELP, ChatColor.GRAY, ChatColor.GRAY, ChatColor.GRAY)
	manager.setFormat(MessageType.SYNTAX, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GRAY)
	manager.setFormat(MessageType.INFO, ChatColor.BLUE, ChatColor.BLUE, ChatColor.BLUE)

	return manager
}

fun BukkitCommandManager.registerCommands(vararg commands: BaseCommand) {
	commands.forEach {
		registerCommand(it, false);
	}
}