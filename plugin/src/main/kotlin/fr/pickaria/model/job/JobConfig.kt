package fr.pickaria.model.job

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
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

val jobConfig = Yaml.default.decodeFromStream<JobConfig>(getResourceFileStream("job.yml"))
