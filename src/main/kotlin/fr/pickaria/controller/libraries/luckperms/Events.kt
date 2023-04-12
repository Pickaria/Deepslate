package fr.pickaria.controller.libraries.luckperms

import net.luckperms.api.event.LuckPermsEvent
import org.bukkit.plugin.java.JavaPlugin

inline fun <reified T : LuckPermsEvent> JavaPlugin.subscribeToLuckPerms(noinline handler: T.() -> Unit) {
	luckPermsProvider.eventBus.subscribe(this, T::class.java) {
		handler.invoke(it)
	}
}
