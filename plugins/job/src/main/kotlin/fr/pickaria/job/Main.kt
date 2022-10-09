package fr.pickaria.job

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		// TODO: Add plugin logic here

		logger.info("Job plugin loaded!")
	}
}
