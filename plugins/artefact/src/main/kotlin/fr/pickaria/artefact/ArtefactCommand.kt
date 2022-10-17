package fr.pickaria.artefact

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType

internal class ArtefactCommand : CommandExecutor, Listener, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			args?.getOrNull(0)?.let {
				Artefact.valueOf(it.uppercase())
			}?.let {
				val itemStack = ItemStack(it.material)

				val itemMeta = itemStack.itemMeta
				if (itemMeta is LeatherArmorMeta) {
					itemMeta.setColor(it.color)
					itemMeta.addItemFlags(ItemFlag.HIDE_DYE)
				}

				itemMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
				itemMeta.persistentDataContainer.set(namespace, PersistentDataType.STRING, it.name)
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
				itemMeta.displayName(it.displayName)
				itemMeta.lore(it.lore)

				itemStack.itemMeta = itemMeta

				sender.inventory.addItem(itemStack)
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>?
	): MutableList<String> {
		return Artefact.values().map { it.name.lowercase() }.toMutableList()
	}

	@EventHandler
	fun onEntityTarget(event: EntityTargetEvent) {
		event.target?.let {
			if (it is Player) {
				if (getWornArtefacts(it).contains(Artefact.STEALTH_CHESTPLATE)) {
					event.isCancelled = true
				}
			}
		}
	}

	@EventHandler
	fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
		event.entity.let { player ->
			if (player is Player) {
				if (getWornArtefacts(player).contains(Artefact.ICE_LEGGINGS)) {
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
		if (getWornArtefacts(event.player).contains(Artefact.LUCKY_PICKAXE)) {
			if (Math.random() < 0.1) {
				event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.DIAMOND, 1))
			}
		}
	}

	@EventHandler
	fun onPlayerMove(event: PlayerMoveEvent) {
		if (getWornArtefacts(event.player).contains(Artefact.FIRE_BOOTS)) {
			val loc = event.player.location
			event.player.world.spawnParticle(Particle.FLAME, loc, 2, 0.1, 0.1, 0.1, 0.01)
		}
	}
}