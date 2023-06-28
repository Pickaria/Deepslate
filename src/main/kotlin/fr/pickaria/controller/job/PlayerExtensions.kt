package fr.pickaria.controller.job

import fr.pickaria.controller.job.events.JobAscentEvent
import fr.pickaria.controller.job.events.JobJoinedEvent
import fr.pickaria.model.job.*
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

	JobJoinedEvent(this, jobType.toJob()).callEvent()
}

/**
 * Sets the given job as not active.
 */
internal infix fun Player.leaveJob(jobType: JobType) {
	JobModel.get(uniqueId, jobType)?.apply {
		active = false
	}
}

data class PlayerRank(
	val jobRank: JobRank?,
	val points: Int,
)

/**
 * Get the job rank of a player.
 */
private val Player.rank: PlayerRank
	get() {
		val ascendPoints = JobModel.get(uniqueId).sumOf { it.ascentPoints }

		for ((points, jobRank) in jobConfig.ranks) {
			if (ascendPoints >= points) {
				return PlayerRank(jobRank, ascendPoints)
			}
		}

		return PlayerRank(null, ascendPoints)
	}

/**
 * Get the suffix of the player's job rank.
 */
private val Player.rankSuffix: Component
	get() {
		val ascendPoints = JobModel.get(uniqueId).sumOf { it.ascentPoints }

		for ((points, jobRank) in jobConfig.ranks) {
			if (ascendPoints >= points) {
				jobRank.suffix?.let {
					val hoverMessage = MiniMessage(jobConfig.rankHover) {
						"points" to points
					}.toComponent()

					return MiniMessage(it).toComponent().hoverEvent(HoverEvent.showText(hoverMessage))
				}
			}
		}

		return Component.empty()
	}

/**
 * Returns the time in minutes before the player can leave the given job.
 */
internal fun Player.getJobCooldown(jobType: JobType): Int {
	val previousDay = Clock.System.now() - jobConfig.jobCooldown.hours
	val job = JobModel.get(uniqueId, jobType)

	return if (job == null || !job.active) {
		0
	} else {
		val lastUsed = job.lastUsed.toInstant(TimeZone.currentSystemDefault())
		previousDay.until(lastUsed, DateTimeUnit.MINUTE).minutes.toInt(DurationUnit.MINUTES)
	}
}

internal fun Player.ascentJob(jobType: JobType): AscentResponse = JobModel.get(uniqueId, jobType)?.let { job ->
	val now = Clock.System.now()
	val canAscent: Boolean = job.lastAscent?.let {
		Clock.System.now() > it.toInstant(TimeZone.currentSystemDefault())
			.plus(jobConfig.ascentCooldown, DateTimeUnit.HOUR)
	} ?: true

	if (!canAscent) {
		return AscentResponse.COOLDOWN
	}

	jobType.toJob().let { config ->
		val ascentPoints = getAscentPoints(job, config)
		if (ascentPoints > 0) {

			job.ascentPoints += ascentPoints
			job.totalExperience += job.experience
			job.experience = 0.0
			job.lastAscent = now.toLocalDateTime(TimeZone.currentSystemDefault())

			JobAscentEvent(this, config, ascentPoints, rank.points, rank.jobRank).callEvent()

			refreshDisplayName()
			AscentResponse.SUCCESS
		} else {
			AscentResponse.NOT_ENOUGH_POINTS
		}
	}
} ?: AscentResponse.NOT_IN_JOB


private val miniMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()

/**
 * Refreshes the player's display name with their job rank.
 */
internal fun Player.refreshDisplayName() {
	suffix = miniMessage.serialize(rankSuffix)
	updateDisplayName()
}

internal fun Player.jobs() = JobModel.get(uniqueId)
