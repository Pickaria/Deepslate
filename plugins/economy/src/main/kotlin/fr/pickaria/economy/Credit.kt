package fr.pickaria.economy

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.event.Listener

object Credit : Currency(), Listener {
	override val economy: Economy = Economy(Config.currencyNameSingular, Config.currencyNamePlural, "default", "0")
	override val material: Material = Material.GOLD_NUGGET
	override val description: List<String> = listOf(Config.currencyDescription)
	override val sound = Sound.sound(Key.key(Config.currencyCollectSound), Sound.Source.MASTER, 1f, 1f)
	override val creditMessage = Config.currencyCollectMessage

	init {
		register()
	}
}