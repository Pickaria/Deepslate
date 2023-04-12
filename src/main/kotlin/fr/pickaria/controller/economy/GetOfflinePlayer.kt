package fr.pickaria.controller.economy

import com.palmergames.bukkit.towny.TownyAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

/**
 * Gets the offline player corresponding to the given name.
 * Checks for towns and nations from Towny as well as regular players with caching support.
 */
fun getOfflinePlayer(playerName: String): OfflinePlayer {
	val offlinePlayer = if (playerName.startsWith("town-")) {
		TownyAPI.getInstance().getTown(playerName.substring(5))?.let {
			Bukkit.getOfflinePlayer(it.uuid)
		}
	} else if (playerName.startsWith("nation-")) {
		TownyAPI.getInstance().getNation(playerName.substring(7))?.let {
			Bukkit.getOfflinePlayer(it.uuid)
		}
	} else {
		Bukkit.getOfflinePlayerIfCached(playerName)
	}

	return offlinePlayer ?: Bukkit.getOfflinePlayer(playerName)
}