package fr.pickaria.job

import fr.pickaria.potion.PotionConfig
import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.text.Component
import org.bukkit.Material

class AscentConfig : ConfigProvider() {
	val startLevel: Int by this
	val pointEvery: Int by this
	val pointAmount: Int by this
	val experienceIncrease: Double by this
	val moneyIncrease: Double by this
}

class JobConfig : ConfigProvider() {
	val label: String by this
	val icon: Material by this
	val description: List<String> by this
	val experiencePercentage: Double by this
	val multiplier: Int by this
	val revenueIncrease: Double by this
	val startExperience: Int by this

	val key: String
		get() = section!!.name
}

object Config : ConfigProvider() {
	val lastPaymentDelay: Long by this
	val maxJobs: Int by this
	val maxLevel: Int by this
	val cooldown: Long by this

	val ascent: AscentConfig by lazy {
		section?.getConfigurationSection("ascent")?.let {
			AscentConfig().apply {
				setConfig(it)
			}
		}!!
	}

	val potion: PotionConfig by this
	val rankHover: String by this
	val jobs by sectionLoader<JobConfig>()

	val ranks: Map<Int, Component> by lazy {
		section!!.getConfigurationSection("ranks")!!.run {
			this.getKeys(false)
				.sortedByDescending { it.toInt() }
				.associateWith { miniMessage.deserialize(this.getString(it)!!) }
				.mapKeys { it.key.toInt() }
		}
	}
}