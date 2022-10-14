package fr.pickaria.shared

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer

fun setupEconomy(): Economy? = getServer().servicesManager.getRegistration(Economy::class.java)?.provider

fun setupChat(): Chat? = getServer().servicesManager.getRegistration(Chat::class.java)?.provider
