package fr.pickaria.economy

val Credit : Currency
	get() = Config.currencies["credits"]!!
val Shard : Currency
	get() = Config.currencies["shards"]!!
val Key : Currency
	get() = Config.currencies["keys"]!!