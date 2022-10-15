package fr.pickaria.teleport

import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Location
import org.bukkit.block.Biome
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.LocalDateTime
import java.util.HashMap
import java.util.Random

class RandomCommand : CommandExecutor {
	companion object {
		private const val TAG = "FIRST_RTP_USED"
		private const val PRICE = 100.0
		private const val BETWEEN_TELEPORT_COOLDOWN = 20 // Cooldown during teleports

		private val EXCLUDED_BIOMES = setOf(
			// Oceans
			Biome.OCEAN,
			Biome.COLD_OCEAN,
			Biome.DEEP_OCEAN,
			Biome.DEEP_COLD_OCEAN,
			Biome.DEEP_FROZEN_OCEAN,
			Biome.DEEP_LUKEWARM_OCEAN,
			Biome.FROZEN_OCEAN,
			Biome.LUKEWARM_OCEAN,
			Biome.WARM_OCEAN,

			// Rivers
			Biome.RIVER,
			Biome.FROZEN_RIVER,

			// Powder snow biomes
//			Biome.SNOWY_SLOPES,
//			Biome.GROVE,
		)
	}

	private val random: Random = Random()
	private val maxRadius = 5
	private val lastTeleport = HashMap<Player, LocalDateTime>()

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val containsTag = sender.scoreboardTags.contains(TAG)

			if (economy != null) {
				if (containsTag && economy.has(sender, PRICE)) {
					val withdrawResponse = economy.withdrawPlayer(sender, PRICE)

					if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
						teleportPlayer(sender)
					} else {
						sender.sendMessage("§cUne erreur s'est produite.")
					}
				} else if (!containsTag) {
					teleportPlayer(sender)
					sender.addScoreboardTag(TAG)
				} else {
					sender.sendMessage("§cVous n'avez pas assez d'argent.")
				}
			}
		}

		return true
	}

	private fun teleportPlayer(sender: Player) {
		var tries = 0
		var x: Int
		var z: Int
		var location: Location

		do {
			x = random.nextInt(maxRadius * 2) - maxRadius
			z = random.nextInt(maxRadius * 2) - maxRadius
			location = Location(sender.world, x.toDouble(), 0.0, z.toDouble())
			location.y = sender.world.getHighestBlockYAt(location).toDouble()
		} while (EXCLUDED_BIOMES.contains(sender.world.getBiome(location)) && tries++ < 5 && !location.block.type.isOccluding)

		location.y += 1.0

		lastTeleport[sender] = LocalDateTime.now()

		teleportController.cooldownTeleport(sender, location)
	}
}