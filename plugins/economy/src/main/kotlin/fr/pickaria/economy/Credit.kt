package fr.pickaria.economy

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import java.util.*

object Credit : Currency() {
	override val economy: Economy = Economy(Config.currencyNameSingular, Config.currencyNamePlural, "default", "Credits")
	override val material: Material = Material.GOLD_NUGGET
	override val currencyDisplayName: Component = Component.text(Config.currencyNameSingular.replaceFirstChar {
		if (it.isLowerCase()) it.titlecase(
			Locale.getDefault()
		) else it.toString()
	}, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
	override val description: String = Config.currencyDescription
	override val sound = Config.currencyCollectSound
	override val creditMessage = Config.currencyCollectMessage

	init {
		register()
	}
}