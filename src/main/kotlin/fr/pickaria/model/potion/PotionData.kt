package fr.pickaria.model.potion

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import java.time.Instant

internal data class PotionData(
	val startedAt: Instant,
	val endAt: Instant,
	val audience: Audience,
	val bossBar: BossBar,
)
