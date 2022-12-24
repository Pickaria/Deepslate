package fr.pickaria.controller.job

import fr.pickaria.controller.job.events.JobAscentEvent
import fr.pickaria.model.advancements.CustomAdvancement
import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobModel
import fr.pickaria.model.job.JobType
import fr.pickaria.model.job.jobConfig
import fr.pickaria.model.now
import fr.pickaria.shared.MiniMessage
import fr.pickaria.shared.suffix
import fr.pickaria.shared.updateDisplayName
import kotlinx.datetime.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * Checks if the player is currently using the given job.
 */
internal infix fun Player.hasJob(jobType: JobType): Boolean = JobModel.get(uniqueId, jobType)?.active == true

/**
 * Returns the amount of active jobs a player has.
 */
internal fun Player.jobCount(): Int = JobModel.get(uniqueId).filter { it.active }.size

/**
 * Sets the current job as `active` and updates the `last used` field.
 */
internal infix fun Player.joinJob(jobType: JobType) {
	JobModel.get(uniqueId, jobType)?.apply {
		active = true
		lastUsed = now()
	} ?: JobModel.create(uniqueId, jobType, true)
	CustomAdvancement.JOIN_JOB.grant(this)
}

/**
 * Sets the given job as not active.
 */
internal infix fun Player.leaveJob(jobType: JobType) {
	JobModel.get(uniqueId, jobType)?.apply {
		active = false
	}
}

/**
 * Get the job rank of a player.
 */
internal fun Player.getRank(): Component {
	val ascendPoints = JobModel.get(uniqueId).sumOf { it.ascentPoints }
	for ((points, suffix) in jobConfig.ranks) {
		if (ascendPoints >= points) {
			val parsedSuffix = MiniMessage(suffix).message.hoverEvent(
				HoverEvent.showText(MiniMessage(jobConfig.rankHover) {
					"points" to ascendPoints.toString()
				}.message)
			)

			return parsedSuffix
		}
	}
	return Component.empty()
}

/**
 * Returns the time in minutes before the player can leave the given job.
 */
internal fun Player.getJobCooldown(jobType: JobType): Int {
	val previousDay = Clock.System.now() - jobConfig.cooldown.hours
	val job = JobModel.get(uniqueId, jobType)

	return if (job == null || !job.active) {
		0
	} else {
		val lastUsed = job.lastUsed.toInstant(TimeZone.currentSystemDefault())
		previousDay.until(lastUsed, DateTimeUnit.MINUTE).minutes.toInt(DurationUnit.MINUTES)
	}
}

internal infix fun Player.ascentJob(jobType: JobType): Boolean = JobModel.get(uniqueId, jobType)?.let { job ->
	jobType.toJob().let { config ->
		val ascentPoints = getAscentPoints(job, config)
		if (ascentPoints > 0) {
			ascentJob(config, job, ascentPoints)
			refreshDisplayName()
			true
		} else {
			false
		}
	}
} ?: false

internal fun Player.ascentJob(config: Job, job: JobModel, ascentPoints: Int) {
	job.ascentPoints += ascentPoints
	job.experience = 0.0

	JobAscentEvent(this, config, ascentPoints).callEvent()
}

private val miniMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()

/**
 * Refreshes the player's display name with their job rank.
 */
internal fun Player.refreshDisplayName() {
	suffix = miniMessage.serialize(getRank())
	updateDisplayName()
}

internal fun Player.jobs() = JobModel.get(uniqueId)
