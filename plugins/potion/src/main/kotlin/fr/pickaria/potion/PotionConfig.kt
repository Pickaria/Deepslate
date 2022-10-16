package fr.pickaria.potion

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.configuration.file.FileConfiguration

class PotionConfig(config: FileConfiguration) {
	data class Configuration(
		val key: String,
		val label: Component,
		val potionColor: Color,
		val bossBarColor: BossBar.Color,
		val duration: Long,
		val description: String,
		val power: Int,
		val lore: List<Component>,
	)

	private fun getColor(color: BossBar.Color): Color =
		when (color) {
			BossBar.Color.PINK -> Color.FUCHSIA
			BossBar.Color.BLUE -> Color.BLUE
			BossBar.Color.RED -> Color.RED
			BossBar.Color.GREEN -> Color.GREEN
			BossBar.Color.YELLOW -> Color.YELLOW
			BossBar.Color.PURPLE -> Color.PURPLE
			BossBar.Color.WHITE -> Color.WHITE
		}

	val potions: Map<String, Configuration> = config.getConfigurationSection("potions")!!
		.getKeys(false)
		.associateWith {
			Bukkit.getLogger().info("Loading potion '$it'")

			val section = config.getConfigurationSection("potions.$it")!!

			val color = section.getString("color")!!
			val bossBarColor = BossBar.Color.valueOf(color)
			val potionColor = getColor(bossBarColor)

			val duration = section.getLong("duration")
			val minutes = duration / 60
			val seconds = duration % 60

			val description = section.getString("description")!!
			val effectName = section.getString("effect_name")!!
			val power = section.getInt("power")

			val lore = listOf<Component>(
				Component.text("$description ($minutes:$seconds)", NamedTextColor.BLUE)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				Component.text(""),
				Component.text("Si consommée :", NamedTextColor.DARK_PURPLE)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				Component.text(effectName, NamedTextColor.BLUE)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			)

			val label = Component.text(section.getString("label")!!, NamedTextColor.WHITE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

			Configuration(
				it,
				label,
				potionColor,
				bossBarColor,
				duration,
				description,
				power,
				lore,
			)
		}
}