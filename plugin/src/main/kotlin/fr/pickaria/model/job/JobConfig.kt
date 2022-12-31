package fr.pickaria.model.job

import fr.pickaria.model.config
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobConfig(
	@SerialName("last_payment_delay")
	val lastPaymentDelay: Long,
	@SerialName("max_jobs")
	val maxJobs: Int,
	@SerialName("max_level")
	val maxLevel: Int,
	val cooldown: Long,
	val ascent: AscentConfig,
	@SerialName("rank_hover")
	val rankHover: String,
	val jobs: Map<String, Job>,
	val ranks: Map<Int, String>,
)

val jobConfig = config<JobConfig>("job.yml")
