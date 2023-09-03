package fr.pickaria.controller.libraries.bukkit

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.full.primaryConstructor

inline fun <reified T> getRegistration() = Bukkit.getServicesManager().getRegistration(T::class.java)

inline fun <reified T : Listener> JavaPlugin.registerEvents(vararg args: Any) {
    val constructor = T::class.primaryConstructor
    if (constructor != null) {
        val params = constructor.parameters
        if (args.size != params.size) {
            throw IllegalArgumentException("Invalid amount of parameters provided.")
        }
        server.pluginManager.registerEvents(constructor.call(*args), this)
    } else {
        throw IllegalArgumentException("No constructor for provided class.")
    }
}
