package fr.pickaria.controller.potion

import fr.pickaria.model.potion.PotionConfig
import fr.pickaria.model.potion.PotionData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.time.Instant

class PotionController(plugin: JavaPlugin) {
	private val activePotionEffects = mutableMapOf<PotionConfig, PotionData>()

	private val runnable: Runnable = Runnable {
		val now = Instant.now()
		val effectsToRemove = mutableListOf<PotionConfig>()

		activePotionEffects.forEach { (effect, data) ->
			val difference = Duration.between(data.startedAt, now).seconds
			val progress = difference / effect.duration.toFloat()
			data.bossBar.progress(progress)

			if (data.endAt.isBefore(now)) {
				data.audience.hideBossBar(data.bossBar)
				effectsToRemove.add(effect)
			}
		}

		effectsToRemove.forEach {
			activePotionEffects.remove(it)
		}
	}

	init {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0, 20)
	}

	internal fun addPotionEffect(config: PotionConfig, activator: Player) {
		val now = Instant.now()
		val audience = Audience.audience(activator)
		val bossBar = BossBar.bossBar(Component.text(config.description), 0F, config.color, BossBar.Overlay.PROGRESS)
		audience.showBossBar(bossBar)

		activePotionEffects[config] = PotionData(
			now,
			now.plusSeconds(config.duration.toLong()),
			audience,
			bossBar
		)
	}

	fun hasActivePotionEffect(config: PotionConfig): Boolean =
		activePotionEffects.containsKey(config)
}