package fr.pickaria.job

import fr.pickaria.chat.suffix
import fr.pickaria.chat.updateDisplayName
import fr.pickaria.database.models.Job
import fr.pickaria.job.events.JobAscentEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * Checks if the player is currently using the given job.
 */
internal infix fun Player.hasJob(jobName: String): Boolean = Job.get(uniqueId, jobName)?.active == true

/**
 * Returns the amount of active jobs a player has.
 */
internal fun Player.jobCount(): Int = Job.get(uniqueId).filter { it.active }.size

/**
 * Sets the current job as `active` and updates the `last used` field.
 */
internal infix fun Player.joinJob(jobName: String) {
	Job.get(uniqueId, jobName)?.apply {
		active = true
		lastUsed = LocalDateTime.now()
	} ?: Job.create(uniqueId, jobName, true)
}

/**
 * Sets the given job as not active.
 */
internal infix fun Player.leaveJob(jobName: String) {
	Job.get(uniqueId, jobName)?.apply {
		active = false
	}
}

/**
 * Get the job rank of a player.
 */
internal fun Player.getRank(): Component {
	val ascendPoints = Job.get(uniqueId).sumOf { it.ascentPoints }
	for ((points, suffix) in jobConfig.ranks) {
		if (ascendPoints >= points) {
			return suffix.hoverEvent(
				HoverEvent.showText(
				miniMessage.deserialize(jobConfig.rankHover, Placeholder.parsed("points", ascendPoints.toString()))
			))
		}
	}
	return Component.empty()
}

/**
 * Returns the time in minutes before the player can leave the given job.
 */
internal fun Player.getJobCooldown(jobName: String): Int {
	val previousDay = LocalDateTime.now().minusHours(jobConfig.cooldown)

	val job = Job.get(uniqueId, jobName)
	return if (job == null || !job.active) {
		0
	} else {
		previousDay.until(job.lastUsed, ChronoUnit.MINUTES).minutes.toInt(DurationUnit.MINUTES)
	}
}

internal infix fun Player.ascentJob(jobName: String): Boolean =
	Job.get(uniqueId, jobName)?.let { job ->
		jobConfig.jobs[jobName]?.let { config ->
			val ascentPoints = getAscentPoints(job, config)
			if (ascentPoints > 0) {
				ascentJob(config, job, ascentPoints)
				refreshDisplayName()
				true
			} else {
				false
			}
		} ?: false
	} ?: false

internal fun Player.ascentJob(config: JobConfig.Configuration, job: Job, ascentPoints: Int) {
	job.ascentPoints += ascentPoints
	job.experience = 0.0

	JobAscentEvent(this, config, ascentPoints).callEvent()
}

/**
 * Refreshes the player's display name with their job rank.
 */
internal fun Player.refreshDisplayName() {
	suffix = miniMessage.serialize(getRank())
	updateDisplayName()
}