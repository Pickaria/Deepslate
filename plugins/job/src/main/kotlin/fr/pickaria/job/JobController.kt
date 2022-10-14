package fr.pickaria.job

import fr.pickaria.job.events.JobAscentEvent
import fr.pickaria.job.events.JobLevelUpEvent
import fr.pickaria.job.jobs.*
import fr.pickaria.shared.models.Job
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

class JobController(private val plugin: Main) : Listener {
	private val bossBars: ConcurrentHashMap<Player, BossBar> = ConcurrentHashMap()
	private val bossBarsTasks: ConcurrentHashMap<BossBar, BukkitTask> = ConcurrentHashMap()

	init {
		getServer().pluginManager.run {
			registerEvents(this@JobController, plugin)

			registerEvents(Miner(), plugin)
			registerEvents(Hunter(), plugin)
			registerEvents(Farmer(), plugin)
			registerEvents(Breeder(), plugin)
			registerEvents(Alchemist(), plugin)
			registerEvents(Wizard(), plugin)
			registerEvents(Trader(), plugin)
		}
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		bossBars.remove(event.player)
	}

	@EventHandler
	fun onJobLevelUp(event: JobLevelUpEvent) {
		val player = event.player
		val level = event.level
		val label = event.job.label
		val type = event.type

		player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
		player.sendMessage("§7Vous montez niveau §6$level§7 dans le métier §6$label§7.")

		if (type == LevelUpType.ASCENT_UNLOCKED) {
			val mainTitle = Component.text("Ascension débloquée", NamedTextColor.GOLD)
			val subtitle = Component.text("Vous avez débloqué l'ascension pour le métier $label !", NamedTextColor.GRAY)
			val title = Title.title(mainTitle, subtitle)

			player.showTitle(title)
		} else if (type == LevelUpType.MAX_LEVEL_REACHED) {
			val mainTitle = Component.text("Niveau maximum atteint", NamedTextColor.GOLD)
			val subtitle = Component.text("Vous avez atteint le niveau maximum dans le métier $label !", NamedTextColor.GRAY)
			val title = Title.title(mainTitle, subtitle)

			player.showTitle(title)
		}
	}

	@EventHandler
	fun onJobAscent(event: JobAscentEvent) {
		val player = event.player
		val label = event.job.label
		val ascentPoints = event.ascentPoints

		val experienceBoost = jobConfig.experienceIncrease * ascentPoints * 100
		val moneyBoost = jobConfig.moneyIncrease * ascentPoints * 100

		player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
		player.sendMessage(
			"§7Vous avez effectué une ascension et collecté §6$ascentPoints§7 points d'ascension dans le métier §6$label§7.\n" +
			"§7Vous obtenez un bonus de §6$experienceBoost%§7 d'expérience ainsi que §6$moneyBoost%§7 de revenus supplémentaires."
		)
	}

	/**
	 * Checks if the player is currently using the given job.
	 */
	fun hasJob(playerUuid: UUID, jobName: String) = Job.get(playerUuid, jobName)?.active == true

	/**
	 * Returns the amount of active jobs a player has.
	 */
	fun jobCount(playerUuid: UUID): Int = Job.get(playerUuid).filter { it.active }.size

	/**
	 * Returns the time in minutes before the player can leave the given job.
	 */
	fun getCooldown(playerUuid: UUID, jobName: String): Int {
		val previousDay = LocalDateTime.now().minusHours(jobConfig.cooldown)

		val job = Job.get(playerUuid, jobName)
		return if (job == null || !job.active) {
			0
		} else {
			previousDay.until(job.lastUsed, ChronoUnit.MINUTES).minutes.toInt(DurationUnit.MINUTES)
		}
	}

	/**
	 * Returns `0` if the player cannot ascent, otherwise returns `> 1`.
	 */
	fun getAscentPoints(job: Job, config: JobConfig.Configuration): Int =
		getLevelFromExperience(config, job.experience).let {
			if (it >= jobConfig.ascentStartLevel) {
				(it - jobConfig.ascentStartLevel) / jobConfig.pointEvery * jobConfig.pointAmount + 1
			} else {
				0
			}
		}

	fun ascentJob(player: Player, jobName: String): Boolean =
		Job.get(player.uniqueId, jobName)?.let { job ->
			jobConfig.jobs[jobName]?.let { config ->
				val ascentPoints = jobController.getAscentPoints(job, config)
				if (ascentPoints > 0) {
					ascentJob(player, config, job, ascentPoints)
					true
				} else {
					false
				}
			} ?: false
		} ?: false

	fun ascentJob(player: Player, config: JobConfig.Configuration, job: Job, ascentPoints: Int) {
		job.ascentPoints += ascentPoints
		job.experience = 0.0

		JobAscentEvent(player, config, ascentPoints).callEvent()
	}

	/**
	 * Sets the current job as `active` and updates the `last used` field.
	 */
	fun joinJob(playerUuid: UUID, jobName: String) {
		Job.get(playerUuid, jobName)?.apply {
			active = true
			lastUsed = LocalDateTime.now()
		} ?: Job.create(playerUuid, jobName, true)
	}

	/**
	 * Sets the given job as not active.
	 */
	fun leaveJob(playerUuid: UUID, jobName: String) {
		Job.get(playerUuid, jobName)?.apply {
			active = false
		}
	}

	/**
	 * Get the amount of experience required for a specific job level.
	 */
	private fun getExperienceFromLevel(job: JobConfig.Configuration, level: Int): Int =
		if (level >= 0) {
			ceil(job.startExperience * job.experiencePercentage.pow(level) + level * job.multiplier).toInt()
		} else {
			0
		}

	/**
	 * Returns the level from a job configuration and experience.
	 * Use with caution as it contains a while loop.
	 */
	fun getLevelFromExperience(job: JobConfig.Configuration, experience: Double): Int {
		var level = 0
		var levelExperience = job.startExperience.toDouble()
		while ((levelExperience + level * job.multiplier) < experience && level < jobConfig.maxLevel) {
			levelExperience *= job.experiencePercentage
			level++
		}
		return level
	}

	/**
	 * Adds experience to a player's job and fire level up events if necessary.
	 */
	private fun addExperience(player: Player, job: JobConfig.Configuration, exp: Int): Pair<Int, Double> {
		return Job.get(player.uniqueId, job.key)?.let {
			val experienceIncrease = exp + exp * it.ascentPoints * jobConfig.experienceIncrease

			val previousLevel = getLevelFromExperience(job, it.experience)
			val newLevel = getLevelFromExperience(job, it.experience + experienceIncrease)

			it.experience += experienceIncrease

			val isNewLevel = newLevel > previousLevel

			if (isNewLevel) {
				val type = if (newLevel == jobConfig.ascentStartLevel) {
					LevelUpType.ASCENT_UNLOCKED
				} else if (newLevel >= jobConfig.maxLevel) {
					LevelUpType.MAX_LEVEL_REACHED
				} else {
					LevelUpType.NEW_LEVEL
				}

				JobLevelUpEvent(player, type, job, newLevel).callEvent()
			}

			(newLevel to it.experience + experienceIncrease)
		} ?: (0 to 0.0)
	}

	/**
	 * Adds experience and displays a boss bar with information.
	 */
	fun addExperienceAndAnnounce(player: Player, job: JobConfig.Configuration, exp: Int) {
		addExperience(player, job, exp).also { (level, experience) ->
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
}
