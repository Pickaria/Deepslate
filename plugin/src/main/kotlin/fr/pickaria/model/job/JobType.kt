package fr.pickaria.model.job

/**
 * All the supported job types by the plugin.
 */
enum class JobType {
	MINER,
	HUNTER,
	FARMER,
	BREEDER,
	ALCHEMIST,
	WIZARD,
	TRADER;

	fun toJob(): Job = jobConfig.jobs[name.lowercase()]!!
}