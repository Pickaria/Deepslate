package fr.pickaria.controller.economy

import org.bukkit.Bukkit

/**
 * Gets the offline player corresponding to the given name.
 * FIXME: Is it still useful?
 */
fun getOfflinePlayer(playerName: String) =
	Bukkit.getOfflinePlayerIfCached(playerName) ?: Bukkit.getOfflinePlayer(playerName)
