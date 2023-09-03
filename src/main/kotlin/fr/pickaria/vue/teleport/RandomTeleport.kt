package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.teleport.teleportToLocationAfterTimeout
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.mainConfig
import fr.pickaria.model.teleport.teleportConfig
import fr.pickaria.shared.MiniMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.cos
import kotlin.math.log2
import kotlin.math.sin
import kotlin.random.Random

@CommandAlias("randomteleport|rtp|tpr")
@CommandPermission("pickaria.command.randomteleport")
class RandomTeleport(private val plugin: JavaPlugin) : BaseCommand() {
    companion object {
        private val EXCLUDED_BLOCKS = setOf(
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

        private val EXCLUDED_MATERIALS = setOf(
            Material.MAGMA_BLOCK,
            Material.CACTUS,
            Material.POWDER_SNOW,
        )
    }

    @Default
    @Description("Vous téléporte aléatoirement autour du spawn. À utiliser avec précautions.")
    @Conditions("can_teleport")
    fun onDefault(player: Player, @Default("1000") maxRadius: UInt) {
        if (player.world == mainConfig.lobbyWorld) {
            throw ConditionFailedException("Cette commande ne peut pas être exécutée ici.")
        }
        val cost = log2(maxRadius.toDouble()) * teleportConfig.rtpMultiplier

        if (player.has(Credit, cost)) {
            val location = getRandomLocation(player, maxRadius.toInt())
            if (location.world == mainConfig.lobbyWorld) {
                throw ConditionFailedException("Cette commande ne peut pas être exécutée ici.")
            }

            val timeout = 200L // 2 seconds
            player.teleportToLocationAfterTimeout(plugin, location, cost, timeout)
        } else {
            MiniMessage("Erreur: <red>Il faut <gold><amount><gray> pour effectuer la téléportation.") {
                "amount" to Credit.economy.format(cost)
            }.send(player)
        }
    }

    private fun getRandomLocation(sender: Player, maxRadius: Int): Location {
        var tries = 0
        var x: Double
        var z: Double
        var location: Location

        do {
            val random = Random.nextDouble() * 2 - 1
            x = cos(random) * maxRadius
            z = sin(random) * maxRadius
            location = Location(sender.world, x, 0.0, z)
            location.y = sender.world.getHighestBlockYAt(location).toDouble()
        } while (tries++ < 10 && (EXCLUDED_BLOCKS.contains(sender.world.getBiome(location)) || !location.block.type.isSolid || EXCLUDED_MATERIALS.contains(
                location.block.type
            ))
        )

        location.y += 1.0

        return location
    }
}

