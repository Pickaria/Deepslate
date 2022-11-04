package fr.pickaria.job

import fr.pickaria.database.models.Job
import fr.pickaria.job.events.JobLevelUpEvent
import fr.pickaria.job.jobs.*
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

class JobController(private val plugin: Main) : Listener {
	private val bossBars: ConcurrentHashMap<Player, BossBar> = ConcurrentHashMap()
	private val bossBarsTasks: ConcurrentHashMap<BossBar, BukkitTask> = ConcurrentHashMap()

	init {
		getServer().pluginManager.run {
			registerEvents(this@JobController, plugin)
			registerEvents(JobListener(), plugin)

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
		event.player.refreshDisplayName()
	}

	/**
	 * Sets the suffix according to the player's job level.
	 */
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		event.player.refreshDisplayName()
	}

	/**
	 * Adds experience to a player's job and fire level up events if necessary.
	 */
	private fun addExperience(player: Player, job: JobConfig.Configuration, exp: Int): Pair<Int, Double>? =
		Job.get(player.uniqueId, job.key)?.let {
			val potionBoost = jobConfig.potion?.let { potion ->
				val active = potionController?.hasActivePotionEffect(potion) ?: false

				if (active) {
					potion.power / 100
				} else {
					1
				}
			} ?: 1

			val experienceIncrease = (exp + exp * it.ascentPoints * jobConfig.experienceIncrease) * potionBoost

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
		}

	/**
	 * Adds experience and displays a boss bar with information.
	 */
	fun addExperienceAndAnnounce(player: Player, job: JobConfig.Configuration, exp: Int) {
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
}
