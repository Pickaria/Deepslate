package fr.pickaria.shared

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		GlowEnchantment.register(this)
	}
}