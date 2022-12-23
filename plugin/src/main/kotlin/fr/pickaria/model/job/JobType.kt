package fr.pickaria.model.job

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