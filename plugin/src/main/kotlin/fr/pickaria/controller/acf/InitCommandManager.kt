package fr.pickaria.controller.acf

import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import fr.pickaria.model.economy.BankAccount
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

fun initCommandManager(plugin: JavaPlugin): PaperCommandManager {
	val manager = PaperCommandManager(plugin)

	manager.locales.defaultLocale = Locale.FRENCH;

	manager.commandCompletions.registerCompletion("accounts") { c: BukkitCommandCompletionContext ->
		BankAccount.getAccounts(c.player.uniqueId)
	}

	manager.setFormat(MessageType.ERROR, ChatColor.RED, ChatColor.GOLD, ChatColor.RED)
	manager.setFormat(MessageType.HELP, ChatColor.GRAY, ChatColor.GRAY, ChatColor.GRAY)
	manager.setFormat(MessageType.SYNTAX, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GRAY)
	manager.setFormat(MessageType.INFO, ChatColor.BLUE, ChatColor.BLUE, ChatColor.BLUE)

	return manager
}