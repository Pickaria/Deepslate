package fr.pickaria.vue.town

import fr.pickaria.controller.town.getTownBook
import fr.pickaria.controller.town.isTownBanner
import fr.pickaria.controller.town.townId
import fr.pickaria.model.town.Town
import fr.pickaria.model.town.Towns
import fr.pickaria.model.town.townConfig
import fr.pickaria.model.town.townNamespace
import fr.pickaria.shared.give
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Banner
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class BannerListeners(private val plugin: JavaPlugin) : Listener {
	private fun showTitle(target: Audience, text: String, subtitle: Component?) {
		val mainTitle: Component = Component.text(text, NamedTextColor.GOLD)

		// Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
		val title: Title = subtitle?.let { Title.title(mainTitle, it) } ?: Title.title(mainTitle, Component.text(""))

		// Send the title to your audience
		target.showTitle(title)
	}

	private fun townCreatedAnimation(location: Location, player: Player, name: Component) {
		showTitle(player, "Ville fondée", name)
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
	fun onBannerBreak(event: BlockBreakEvent) {
		with(event) {
			block.townId?.let {
				player.sendMessage("VILLE SUPPRIMÉE")
				transaction {
					Town[it].delete()
				}
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

				// TODO: Create a TownController that makes all the requests and processing for towns
				transaction {
					Town.find {
						Towns.identifier eq displayName
					}.firstOrNull()
				}?.let {
					player.sendMessage(townConfig.townNameExist)
					setBuild(false)
					return
				}

				val town =
					transaction {
						Town.new {
							identifier = displayName
							flag = itemInHand
						}
					}

				townCreatedAnimation(block.location, player, itemInHand.itemMeta.displayName() ?: Component.empty())

				val banner = block.state as Banner
				banner.persistentDataContainer.set(
					townNamespace,
					PersistentDataType.INTEGER,
					town.id.value
				)
				banner.update()

				val book = getTownBook(town, player)
				player.give(book)
			}
		}
	}
}