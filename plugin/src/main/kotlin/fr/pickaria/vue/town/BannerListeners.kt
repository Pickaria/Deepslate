package fr.pickaria.vue.town

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.TownyUniverse
import com.palmergames.bukkit.towny.command.TownCommand
import com.palmergames.bukkit.towny.exceptions.TownyException
import fr.pickaria.controller.town.isTownBanner
import fr.pickaria.controller.town.townId
import fr.pickaria.model.town.flag
import fr.pickaria.model.town.townConfig
import fr.pickaria.plugin
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class BannerListeners : Listener {
	private fun Audience.showTitle(title: Component, subtitle: Component = Component.empty()) {
		showTitle(Title.title(title, subtitle))
	}

	private fun townCreatedAnimation(location: Location, player: Player, name: Component) {
		player.showTitle(Component.text("Ville fondée", NamedTextColor.GOLD), name)
		player.playSound(
			Sound.sound(
				Key.key("ui.toast.challenge_complete"),
				Sound.Source.MASTER,
				1.0F,
				1.0F
			)
		)

		var radius = 0

		object : BukkitRunnable() {
			override fun run() {
				val circumference = (PI * radius * 2).toInt()
				for (i in 0..circumference) {
					val x = cos(i.toDouble()) * radius
					val z = sin(i.toDouble()) * radius

					val location = location.toCenterLocation()
						.add(x, 0.0, z)

					location.y = (location.world.getHighestBlockYAt(location) + 1).toDouble()

					location.world.spawnParticle(Particle.GLOW, location, 5, 0.5, 0.0, 0.5, 0.0)
				}

				if (radius++ >= 16) {
					this.cancel()
				}
			}
		}.runTaskTimer(plugin, 1, 1)
	}

	@EventHandler
	fun onBannerBreak(event: BlockDropItemEvent) {
		with(event) {
			blockState.townId?.let { townId ->
				items.find { item ->
					item.itemStack.itemMeta is BannerMeta
				}?.let { item ->
					item.itemStack.townId = townId
				}
			}
		}
	}

	@EventHandler
	fun onBannerPlaced(event: BlockPlaceEvent) {
		with(event) {
			itemInHand.townId?.let { townId ->
				TownyAPI.getInstance().getTown(block.location)?.let { town ->
					if (town.uuid == townId) {
						block.townId = townId

						// TODO: Move town home block and spawn
					} else {
						setBuild(false)
						isCancelled = false

						MiniMessage("<red>La bannière d'une ville doit être placée dans la ville.")
							.send(player)
					}
				}
			} ?: if (itemInHand.isTownBanner()) {
				if (!itemInHand.itemMeta.hasDisplayName()) {
					player.sendMessage(townConfig.townNameMissing)
					setBuild(false)
					return
				}

				val displayName = itemInHand.itemMeta.displayName

				val town = try {
					TownCommand.newTown(
						player,
						displayName,
						TownyUniverse.getInstance().getResident(player.uniqueId),
						true
					)

					TownyAPI.getInstance().getTown(displayName)
				} catch (exception: TownyException) {
					val message = exception.getMessage(player)
					player.sendMessage(message)
					setBuild(false)
					isCancelled = true
					return
				} catch (exception: Exception) {
					player.sendMessage(Component.text("Une erreur inconnue est survenue.", NamedTextColor.RED))
					exception.printStackTrace()
					setBuild(false)
					isCancelled = true
					return
				}

				if (town != null) {
					town.flag = itemInHand
					town.save()

					townCreatedAnimation(block.location, player, itemInHand.itemMeta.displayName() ?: Component.empty())

					block.townId = town.uuid
				} else {
					// This should never happen
					player.sendMessage(Component.text("La ville n'a pas été créée.", NamedTextColor.RED))
					setBuild(false)
					isCancelled = true
					return
				}
			}
		}
	}
}