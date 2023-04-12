package fr.pickaria.model.economy

val Credit: Currency
	get() = economyConfig.currencies["credits"]!!
val Shard: Currency
	get() = economyConfig.currencies["shards"]!!
val Key: Currency
	get() = economyConfig.currencies["keys"]!!