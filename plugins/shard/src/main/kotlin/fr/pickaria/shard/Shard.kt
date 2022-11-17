package fr.pickaria.shard

import fr.pickaria.economy.Currency
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Shard: Currency() {
	override val material: Material = Material.ECHO_SHARD
	override val description: List<String> = Config.pickariteLore
	override val currencyNameSingular: String = Config.currencyNameSingular
	override val currencyNamePlural: String = Config.currencyNamePlural
	override val account: String = "shard"
	override val format: String = "0"

	override fun collect(player: OfflinePlayer, itemStack: ItemStack): EconomyResponse {
		return super.collect(player, itemStack).also {
			if (player is Player) {
				val placeholder = Placeholder.unparsed("amount", Shard.economy.format(it.amount))
				val message = miniMessage.deserialize(Config.collectShardMessage, placeholder)
				player.sendMessage(message)

				player.playSound(Config.grindSound)
				player.location.world.spawnParticle(Particle.END_ROD, player.location.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
			}
		}
	}
}
