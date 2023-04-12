package fr.pickaria.vue.artefact

import fr.pickaria.controller.artefact.isWearingArtefact
import fr.pickaria.model.artefact.ArtefactType
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack


internal class ArtefactListeners : Listener {
	// TODO: Check that artefact is in the correct slot, i.e. an artefact on boots held in hand should not have any effect

	@EventHandler
	fun onEntityTarget(event: EntityTargetEvent) {
		event.target?.let {
			if (it is Player && it.isWearingArtefact(ArtefactType.STEALTH)) {
				event.isCancelled = true
			}
		}
	}

	@EventHandler
	fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
		event.entity.let { player ->
			if (player is Player && player.isWearingArtefact(ArtefactType.ICE_THORNS)) {
				val attacker: Entity? = if (event.damager is Projectile) {
					val shooter = (event.damager as Projectile).shooter
					if (shooter is Entity) {
						shooter
					} else {
						null
					}
				} else {
					event.damager
				}

				attacker?.let {
					it.freezeTicks = it.freezeTicks + 140 // 140 is the minimum to freeze
					it.world.spawnParticle(Particle.SNOWFLAKE, it.location, 10, 0.5, 0.5, 0.5, 0.01)
				}
			}
		}
	}

	@EventHandler
	fun onBlockBreak(event: BlockBreakEvent) {
		with(event) {
			if (player.isWearingArtefact(ArtefactType.LUCKY) && Math.random() < 0.1) {
				block.world.dropItemNaturally(event.block.location, ItemStack(Material.DIAMOND, 1))
			}
		}
	}

	@EventHandler
	fun onPlayerMove(event: PlayerMoveEvent) {
		with(event) {
			if (player.isWearingArtefact(ArtefactType.FLAME_COSMETICS)) {
				player.world.spawnParticle(Particle.FLAME, player.location, 2, 0.1, 0.1, 0.1, 0.01)
			}

			if (player.isWearingArtefact(ArtefactType.EXPLOSION_COSMETICS)) {
				if (player.fallDistance > 0 && player.isOnGround) {
					Particle.FIREWORKS_SPARK.builder()
						.location(player.location)
						.count(10)
						.offset(0.0, 0.0, 0.0)
						.extra(0.1)
						.spawn()
				}
			}
		}
	}
}