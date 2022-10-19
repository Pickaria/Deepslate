package fr.pickaria.economy

import org.bukkit.configuration.file.FileConfiguration

internal class EconomyConfig(config: FileConfiguration) {
	val currencyNameSingular = config.getString("currency_name_singular")!!
	val currencyNamePlural = config.getString("currency_name_plural")!!
}