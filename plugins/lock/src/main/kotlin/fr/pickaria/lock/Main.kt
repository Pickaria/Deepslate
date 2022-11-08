package fr.pickaria.lock

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()

        // TODO: Add plugin logic here

        logger.info("Lock plugin loaded!")
    }
}