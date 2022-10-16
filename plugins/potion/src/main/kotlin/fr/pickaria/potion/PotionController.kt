package fr.pickaria.potion

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.time.Instant

class PotionController(private val plugin: JavaPlugin) {
	private val activePotionEffects = mutableMapOf<PotionType, PotionData>()

	private val runnable: Runnable = Runnable {
		val now = Instant.now()
		val effectsToRemove = mutableListOf<PotionType>()

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

	internal fun addPotionEffect(effect: PotionType, activator: Player) {
		val now = Instant.now()
		val audience = Audience.audience(activator)
		val bossBar = BossBar.bossBar(Component.text(effect.label), 0F, effect.bossBarColor, BossBar.Overlay.PROGRESS)
		audience.showBossBar(bossBar)

		activePotionEffects[effect] = PotionData(
			now,
			now.plusSeconds(effect.duration),
			audience,
			bossBar
		)
	}
}