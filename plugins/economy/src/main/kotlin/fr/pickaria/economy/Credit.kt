package fr.pickaria.economy

import org.bukkit.Material

object Credit : Currency() {
	override val material: Material = Material.GOLD_NUGGET
	override val description: List<String> = listOf(Config.currencyDescription)
	override val currencyNameSingular: String = Config.currencyNameSingular
	override val currencyNamePlural: String = Config.currencyNamePlural
	override val account: String = "default"
	override val format: String = "0.00"
}