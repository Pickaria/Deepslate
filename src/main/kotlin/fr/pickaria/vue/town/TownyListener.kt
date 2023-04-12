package fr.pickaria.vue.town

import com.palmergames.bukkit.towny.event.NewTownEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TownyListener : Listener {
	@EventHandler
	fun onNewTown(event: NewTownEvent) {
		event.town.mayor.player
	}
}