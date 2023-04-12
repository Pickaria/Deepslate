package fr.pickaria.controller.libraries.bukkit

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.full.primaryConstructor

inline fun <reified T> getRegistration() = Bukkit.getServicesManager().getRegistration(T::class.java)

inline fun <reified T : Listener> JavaPlugin.registerEvents() {
	server.pluginManager.registerEvents(T::class.primaryConstructor!!.call(), this)
}
