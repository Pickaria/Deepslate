package fr.pickaria.shard

import fr.pickaria.economy.Currency
import fr.pickaria.economy.Economy
import net.kyori.adventure.sound.Sound
import org.bukkit.Material

object Shard: Currency() {
	override val material: Material = Material.ECHO_SHARD
	override val description: List<String> = shopConfig.pickariteLore
	override val currencyNameSingular: String = shopConfig.currencyNameSingular
	override val currencyNamePlural: String = shopConfig.currencyNamePlural
	override val account: String = "shard"
	override val format: String = "0"
}
