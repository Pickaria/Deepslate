package fr.pickaria.lock

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()

        server.pluginManager.registerEvents(Lock(), this)

        logger.info("Lock plugin loaded!")
    }
}
