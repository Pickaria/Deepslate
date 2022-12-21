package fr.pickaria.artefact

import fr.pickaria.Config
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
	// TODO: Check that artefact is in the correct slot, ie. an artefact on boots held in hand should not have any effect
	private val stealthArtefact = Config.lazyArtefacts["stealth"]!!
	private val iceThornsArtefact = Config.lazyArtefacts["ice_thorns"]!!
	private val luckyArtefact = Config.lazyArtefacts["lucky"]!!
	private val flameCosmeticsArtefact = Config.lazyArtefacts["flame_cosmetics"]!!

	@EventHandler
	fun onEntityTarget(event: EntityTargetEvent) {
		event.target?.let {
			if (it is Player && it.isWearingArtefact(stealthArtefact)) {
				event.isCancelled = true
			}
		}
	}

	@EventHandler
	fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
		event.entity.let { player ->
			if (player is Player && player.isWearingArtefact(iceThornsArtefact)) {
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
			if (player.isWearingArtefact(luckyArtefact) && Math.random() < 0.1) {
				block.world.dropItemNaturally(event.block.location, ItemStack(Material.DIAMOND, 1))
			}
		}
	}

	@EventHandler
	fun onPlayerMove(event: PlayerMoveEvent) {
		with(event) {
			if (player.isWearingArtefact(flameCosmeticsArtefact)) {
				player.world.spawnParticle(Particle.FLAME, player.location, 2, 0.1, 0.1, 0.1, 0.01)
			}
		}
	}
}