package fr.pickaria.job

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class JobConfig(config: FileConfiguration) {
	data class Configuration(
		val key: String,
		val label: String,
		val icon: Material,
		val description: List<String>,
		val experiencePercentage: Double,
		val multiplier: Int,
		val revenueIncrease: Double,
		val startExperience: Int,
	)

	val jobs: Map<String, Configuration> = config.getConfigurationSection("jobs")!!
		.getKeys(false)
		.associateWith {
			Bukkit.getLogger().info("Loading job '$it'")

			val section = config.getConfigurationSection("jobs.$it")!!

			Configuration(
				it,
				section.getString("label")!!,
				Material.getMaterial(section.getString("icon")!!)!!,
				section.getStringList("description"),
				section.getDouble("experience_percentage"),
				section.getInt("multiplier"),
				section.getDouble("revenue_increase"),
				section.getInt("start_experience"),
			)
		}

	val lastPaymentDelay = config.getLong("last_payment_delay")
	val maxJobs = config.getInt("max_jobs")
	val maxLevel = config.getInt("max_level")
	val cooldown = config.getLong("cooldown")

	val ascentStartLevel = config.getInt("ascent.start_level")
	val pointEvery = config.getInt("ascent.point_every")
	val pointAmount = config.getInt("ascent.point_amount")
	val experienceIncrease = config.getDouble("ascent.experience_increase")
	val moneyIncrease = config.getDouble("ascent.money_increase")
}