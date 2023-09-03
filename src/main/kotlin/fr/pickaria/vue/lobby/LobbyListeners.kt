package fr.pickaria.vue.lobby

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent
import fr.pickaria.model.mainConfig
import fr.pickaria.plugin
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.metadata.FixedMetadataValue

private const val KEY = "pickaria:last_ground"

class LobbyListeners : Listener {
    /**
     * Change player's gamemode to adventure when they enter the lobby
     */
    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        with(event) {
            if (player.world == mainConfig.lobbyWorld) {
                player.gameMode = GameMode.ADVENTURE
            } else if (from == mainConfig.lobbyWorld) {
                player.gameMode = GameMode.SURVIVAL
            }
        }
    }

    /**
     * Teleport the player back to its spawn point if they enter a nether portal in the lobby
     */
    @EventHandler
    fun onPlayerPortal(event: PlayerPortalEvent) {
        with(event) {
            if (from.world == mainConfig.lobbyWorld) {
                val spawnLocation = player.bedSpawnLocation ?: mainConfig.overworldWorld?.spawnLocation

                spawnLocation?.let {
                    player.teleport(it)
                } ?: player.sendMessage(Component.text("La surface est inaccessible"))

                isCancelled = true
            }
        }
    }

    /**
     * Prevent setting spawn location in lobby world
     */
    @EventHandler
    fun onPlayerSetSpawn(event: PlayerSetSpawnEvent) {
        with(event) {
            if (player.world == mainConfig.lobbyWorld) {
                isCancelled = true
            }
        }
    }

    /**
     * Prevent all entities from taking damage in lobby
     */
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        with(event) {
            if (entity.world == mainConfig.lobbyWorld) {
                isCancelled = true
            }
        }
    }

    /**
     * Respawn player if they fall too low in lobby
     */
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        with(event) {
            if (player.gameMode == GameMode.ADVENTURE && player.world == mainConfig.lobbyWorld) {
                if (player.isOnGround) {
                    player.setMetadata(KEY, FixedMetadataValue(plugin, player.location))
                } else if (from.toHighestLocation().y <= player.location.world.minHeight) {
                    player.getMetadata(KEY).firstOrNull()?.let {
                        val lastGround = it.value() as Location
                        if (lastGround.y - player.location.y >= 16 || to.y <= 0) {
                            to = lastGround.setDirection(from.direction)
                            player.playSound(to, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                            to.world.spawnParticle(Particle.PORTAL, to.add(0.0, 1.0, 0.0), 20, 0.0, 0.0, 0.0, 1.0)
                        }
                    }
                }
            }
        }
    }
}