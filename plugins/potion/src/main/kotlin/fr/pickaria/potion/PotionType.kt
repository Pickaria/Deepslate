package fr.pickaria.potion

import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Color

/**
 * duration in seconds
 */
enum class PotionType(val label: String, val color: Color, val bossBarColor: BossBar.Color, val duration: Long) {
	JOB_EXPERIENCE_BOOST("Potion de Métier", Color.YELLOW, BossBar.Color.YELLOW, 15L),
}