package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Credit : Currency() {
	override val material: Material = Material.GOLD_NUGGET
	override val description: List<String> = listOf(Config.currencyDescription)
	override val currencyNameSingular: String = Config.currencyNameSingular
	override val currencyNamePlural: String = Config.currencyNamePlural
	override val account: String = "default"
	override val format: String = "0.00"

	override fun collect(player: OfflinePlayer, itemStack: ItemStack): EconomyResponse {
		return super.collect(player, itemStack).also {
			if (player is Player) {
				val placeholder = Placeholder.unparsed("amount", fr.pickaria.economy.economy.format(it.amount))
				player.sendMessage(miniMessage.deserialize(Config.currencyCollectMessage, placeholder))
				player.playSound(Config.currencyCollectSound)
			}
		}
	}
}