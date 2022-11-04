package fr.pickaria.job

import fr.pickaria.database.models.Job
import kotlin.math.ceil
import kotlin.math.pow

/**
 * Get the amount of experience required for a specific job level.
 */
internal fun getExperienceFromLevel(job: JobConfig.Configuration, level: Int): Int =
	if (level >= 0) {
		ceil(job.startExperience * job.experiencePercentage.pow(level) + level * job.multiplier).toInt()
	} else {
		0
	}

/**
 * Returns the level from a job configuration and experience.
 * Use with caution as it contains a while loop.
 */
internal fun getLevelFromExperience(job: JobConfig.Configuration, experience: Double): Int {
	var level = 0
	var levelExperience = job.startExperience.toDouble()
	while ((levelExperience + level * job.multiplier) < experience && level < jobConfig.maxLevel) {
		levelExperience *= job.experiencePercentage
		level++
	}
	return level
}

/**
 * Returns `0` if the player cannot ascent, otherwise returns `> 1`.
 */
internal fun getAscentPoints(job: Job, config: JobConfig.Configuration): Int =
	getLevelFromExperience(config, job.experience).let {
		if (it >= jobConfig.ascentStartLevel) {
			(it - jobConfig.ascentStartLevel) / jobConfig.pointEvery * jobConfig.pointAmount + 1
		} else {
			0
		}
	}