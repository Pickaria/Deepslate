package fr.pickaria.chat

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
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
		e.motd(miniMessage.deserialize(chatConfig.motd, Placeholder.unparsed("time", time)))
	}
}