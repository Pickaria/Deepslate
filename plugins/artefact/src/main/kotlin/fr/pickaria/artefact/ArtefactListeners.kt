package fr.pickaria.artefact

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
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

internal class ArtefactListeners: Listener {
	@EventHandler
	fun onEntityTarget(event: EntityTargetEvent) {
		event.target?.let {
			if (it is Player) {
				if (getWornArtefacts(it).containsValue(Artefact.STEALTH)) {
					event.isCancelled = true
				}
			}
		}
	}

	@EventHandler
	fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
		event.entity.let { player ->
			if (player is Player) {
				if (getWornArtefacts(player).containsValue(Artefact.ICE_THORNS)) {
					val damager: Entity? = if (event.damager is Projectile) {
						val shooter = (event.damager as Projectile).shooter
						if (shooter is Entity) {
							shooter
						} else {
							null
						}
					} else {
						event.damager
					}

					damager?.let {
						it.freezeTicks = it.freezeTicks + 140 // 140 is the minimum to freeze
						it.world.spawnParticle(Particle.SNOWFLAKE, it.location, 10, 0.5, 0.5, 0.5, 0.01)
					}
				}
			}
		}
	}

	@EventHandler
	fun onBlockBreak(event: BlockBreakEvent) {
		if (getWornArtefacts(event.player).containsValue(Artefact.LUCKY)) {
			if (Math.random() < 0.1) {
				event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.DIAMOND, 1))
			}
		}
	}

	@EventHandler
	fun onPlayerMove(event: PlayerMoveEvent) {
		val wornArtefacts = getWornArtefacts(event.player)

		wornArtefacts.forEach { (slot, artefact) ->
			val loc = when (slot) {
				EquipmentSlot.HAND -> event.player.eyeLocation
				EquipmentSlot.OFF_HAND -> event.player.eyeLocation
				EquipmentSlot.FEET -> event.player.location
				EquipmentSlot.LEGS -> event.player.location.clone().add(0.0, 0.5, 0.0)
				EquipmentSlot.CHEST -> event.player.location.clone().add(0.0, 1.0, 0.0)
				EquipmentSlot.HEAD -> event.player.eyeLocation
			}

			if (artefact == Artefact.FLAME_COSMETICS) {
				event.player.world.spawnParticle(Particle.FLAME, loc, 2, 0.1, 0.1, 0.1, 0.01)
			} else if (artefact == Artefact.SNOWFLAKE_COSMETICS) {
				event.player.world.spawnParticle(Particle.EXPLOSION_NORMAL, loc, 2, 0.1, 0.1, 0.1, 0.01)
			}
		}
	}
}