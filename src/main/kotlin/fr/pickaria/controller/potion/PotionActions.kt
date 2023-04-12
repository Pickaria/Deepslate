package fr.pickaria.controller.potion

import fr.pickaria.model.potion.Potion
import fr.pickaria.model.potion.PotionData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant

private val activePotionEffects = mutableMapOf<Potion, PotionData>()

val runnable = Runnable {
	val now = Instant.now()
	val effectsToRemove = mutableListOf<Potion>()

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

internal fun addPotionEffect(config: Potion, activator: Player) {
	val now = Instant.now()
	val audience = Audience.audience(activator)
	val bossBar = BossBar.bossBar(Component.text(config.description), 0F, config.color, BossBar.Overlay.PROGRESS)
	audience.showBossBar(bossBar)

	// TODO: Remove previous boss bar

	activePotionEffects[config] = PotionData(
		now,
		now.plusSeconds(config.duration.toLong()),
		audience,
		bossBar
	)
}

fun hasActivePotionEffect(config: Potion): Boolean =
	activePotionEffects.containsKey(config)