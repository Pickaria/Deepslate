package fr.pickaria.controller.job

import fr.pickaria.Main
import fr.pickaria.controller.job.events.JobLevelUpEvent
import fr.pickaria.controller.potion.hasActivePotionEffect
import fr.pickaria.controller.reward.addDailyPoint
import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobModel
import fr.pickaria.model.job.LevelUpType
import fr.pickaria.model.job.jobConfig
import fr.pickaria.model.potion.PotionType
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

val bossBars: ConcurrentHashMap<Player, BossBar> = ConcurrentHashMap()
private val bossBarsTasks: ConcurrentHashMap<BossBar, BukkitTask> = ConcurrentHashMap()


/**
 * Adds experience to a player's job and fire level up events if necessary.
 */
private fun addExperience(player: Player, job: Job, exp: Int): Pair<Int, Double>? =
	JobModel.get(player.uniqueId, job.type)?.let {
		player.addDailyPoint(exp)
		val potion = PotionType.JOB_EXPERIENCE.toPotion()
		val active = hasActivePotionEffect(potion)

		val potionBoost = if (active) {
			potion.power / 100
		} else {
			1
		}

		val experienceIncrease = (exp + exp * it.ascentPoints * jobConfig.ascent.experienceIncrease) * potionBoost

		val previousLevel = getLevelFromExperience(job, it.experience)
		val newLevel = getLevelFromExperience(job, it.experience + experienceIncrease)

		it.experience += experienceIncrease

		val isNewLevel = newLevel > previousLevel

		if (isNewLevel) {
			val type = if (newLevel == jobConfig.ascent.startLevel) {
				LevelUpType.ASCENT_UNLOCKED
			} else if (newLevel >= jobConfig.maxLevel) {
				LevelUpType.MAX_LEVEL_REACHED
			} else {
				LevelUpType.NEW_LEVEL
			}

			JobLevelUpEvent(player, type, job, newLevel).callEvent()
		}

		(newLevel to it.experience + experienceIncrease)
	}

private val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)

/**
 * Adds experience and displays a boss bar with information.
 */
fun addExperienceAndAnnounce(player: Player, job: Job, exp: Int) {
	addExperience(player, job, exp)?.also { (level, experience) ->
		val currentLevelExperience = getExperienceFromLevel(job, level - 1)
		val nextLevelExperience = getExperienceFromLevel(job, level)
		val levelDiff = abs(nextLevelExperience - currentLevelExperience)
		val diff = abs(experience - currentLevelExperience)

		val bossBar: BossBar = bossBars[player] ?: run {
			val bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID)
			bossBar.addPlayer(player)
			bossBars[player] = bossBar
			bossBar
		}

		bossBar.setTitle("${job.label} | Niveau $level (${experience.toInt()} / $nextLevelExperience)")
		bossBar.isVisible = true
		bossBar.progress = (diff / levelDiff.toDouble()).coerceAtLeast(0.0).coerceAtMost(1.0)

		bossBarsTasks[bossBar]?.cancel()

		bossBarsTasks[bossBar] = object : BukkitRunnable() {
			override fun run() {
				bossBar.isVisible = false
			}
		}.runTaskLater(plugin, 80)
	}
}
