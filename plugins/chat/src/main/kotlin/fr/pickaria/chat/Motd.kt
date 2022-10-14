package fr.pickaria.chat

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent


internal class Motd: Listener {
	@EventHandler
	fun onServerListPing(e: ServerListPingEvent) {
		val world = Bukkit.getServer().getWorld("world")
		val ticks = world!!.time
		val hours = (ticks / 1000 + 6) % 24
		val minutes = ticks % 1000 * 60 / 1000

		val time = String.format("%02d:%02d", hours, minutes)
		e.motd = "§r                §k||§r §6§lPickaria§r §f—§7 §7$time §k||§r§r\n        §7Semi-RP §f|§7 Towny §f|§7 Economy §f|§7 Jobs"
	}
}