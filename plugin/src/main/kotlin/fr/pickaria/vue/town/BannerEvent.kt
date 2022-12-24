package fr.pickaria.vue.town

import fr.pickaria.controller.town.getTownBook
import fr.pickaria.controller.town.isTownBanner
import fr.pickaria.controller.town.townId
import fr.pickaria.model.town.Town
import fr.pickaria.model.town.townConfig
import fr.pickaria.model.town.townNamespace
import fr.pickaria.shared.give
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Particle
import org.bukkit.block.Banner
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class BannerEvent(private val plugin: JavaPlugin) : Listener {
	private fun showTitle(target: Audience, text: String, subtitle: Component?) {
		val mainTitle: Component = Component.text(text, NamedTextColor.GOLD)

		// Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
		val title: Title = subtitle?.let { Title.title(mainTitle, it) } ?: Title.title(mainTitle, Component.text(""))

		// Send the title to your audience
		target.showTitle(title)
	}

	@EventHandler
	fun onBannerBreak(event: BlockBreakEvent) {
		with(event) {
			block.townId?.let {
				player.sendMessage("VILLE SUPPRIMÉE")
				Town.get(it)?.delete()
			}
		}
	}

	@EventHandler
	fun onBannerPlaced(event: BlockPlaceEvent) {
		with(event) {
			if (itemInHand.isTownBanner()) {
				if (!itemInHand.itemMeta.hasDisplayName()) {
					player.sendMessage(townConfig.townNameMissing)
					setBuild(false)
					return
				}

				val displayName = itemInHand.itemMeta.displayName

				Town.get(displayName)?.let {
					player.sendMessage(townConfig.townNameExist)
					setBuild(false)
					return
				}

				Town.create(displayName, itemInHand)?.let {
					val name = itemInHand.itemMeta.displayName()!!

					showTitle(event.player, "Ville fondée", name)
					event.player.playSound(
						Sound.sound(
							Key.key("ui.toast.challenge_complete"),
							Sound.Source.MASTER,
							1.0F,
							1.0F
						)
					)

					var radius = 0

					val banner = block.state as Banner
					banner.persistentDataContainer.set(
						townNamespace,
						PersistentDataType.INTEGER,
						it.id
					)
					banner.update()

					object : BukkitRunnable() {
						override fun run() {
							val circumference = (PI * radius * 2).toInt()
							for (i in 0..circumference) {
								val x = cos(i.toDouble()) * radius
								val z = sin(i.toDouble()) * radius

								val location = event.block.location.toCenterLocation()
									.add(x, 0.0, z)

								location.y = (location.world.getHighestBlockYAt(location) + 1).toDouble()

								event.player.world.spawnParticle(Particle.GLOW, location, 5, 0.5, 0.0, 0.5, 0.0)
							}

							if (radius++ >= 16) {
								this.cancel()
							}
						}
					}.runTaskTimer(plugin, 1, 1)

					val book = getTownBook(it, player)
					player.give(book)
				} ?: run {
					player.sendMessage(townConfig.townNotCreated)
					setBuild(false)
					return
				}
			}
		}
	}
}