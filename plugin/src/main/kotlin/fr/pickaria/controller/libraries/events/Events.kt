package fr.pickaria.controller.libraries.events

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

interface EventListener<in T : Event> : Listener {
	fun onEvent(event: T)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> JavaPlugin.on(
	priority: EventPriority = EventPriority.NORMAL,
	ignoreCancelled: Boolean = false,
	noinline handler: T.() -> Unit
) {
	val listener = object : EventListener<T> {
		override fun onEvent(event: T) {
			handler.invoke(event)
		}
	}

	server.pluginManager.registerEvent(
		T::class.java,
		listener,
		priority,
		{ listener, event ->
			(listener as EventListener<T>).onEvent(event as T)
		},
		this,
		ignoreCancelled
	)
}
