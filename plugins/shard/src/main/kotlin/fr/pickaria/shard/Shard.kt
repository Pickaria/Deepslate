package fr.pickaria.shard

import fr.pickaria.economy.Currency
import fr.pickaria.economy.Economy
import net.kyori.adventure.sound.Sound
import org.bukkit.Material

object Shard: Currency() {
	override val economy: Economy = Economy(shopConfig.currencyNameSingular, shopConfig.currencyNamePlural, "shard", "0")
	override val material: Material = Material.ECHO_SHARD
	override val description: List<String> = shopConfig.pickariteLore
	override val sound: Sound = shopConfig.grindSound
}
