package fr.pickaria.model.advancements

import fr.pickaria.shared.grantAdvancement
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

enum class CustomAdvancement(private val namespace: NamespacedKey?) {
	NINE_LIVES(NamespacedKey.fromString("pickaria:9_lives")),
	INTEGER_LIMIT(NamespacedKey.fromString("pickaria:32bit_limit")),
	BUNDLE_FULL_OF_SHARDS(NamespacedKey.fromString("pickaria:64_shards")),
	OBTAIN_ARTEFACT(NamespacedKey.fromString("pickaria:artefact")),
	RECYCLE_ARTEFACT(NamespacedKey.fromString("pickaria:artefact_recycle")),
	ASCEND_JOB(NamespacedKey.fromString("pickaria:ascend")),
	BILLIONAIRE(NamespacedKey.fromString("pickaria:billionaire")),
	CREATE_TOWN(NamespacedKey.fromString("pickaria:create_town")),
	EPIC_REFORGE(NamespacedKey.fromString("pickaria:epic_stuff")),
	FIRST_CREDIT(NamespacedKey.fromString("pickaria:first_credit")),
	FIRST_RESIDENT(NamespacedKey.fromString("pickaria:first_resident")),
	FIRST_SHARD(NamespacedKey.fromString("pickaria:first_shard")),
	JOIN_JOB(NamespacedKey.fromString("pickaria:join_job")),
	JOIN_TOWN(NamespacedKey.fromString("pickaria:join_town")),
	LEGENDARY_REFORGE(NamespacedKey.fromString("pickaria:legendary_stuff")),
	MARKET_BUY(NamespacedKey.fromString("pickaria:market_buy")),
	MARKET_SELL(NamespacedKey.fromString("pickaria:market_sell")),
	MAXIMUM_JOB_LEVEL(NamespacedKey.fromString("pickaria:maximum_level")),
	MAXIMUM_JOB_LEVEL_IN_ALL_JOBS(NamespacedKey.fromString("pickaria:maximum_level_in_all_jobs")),
	MILLIONAIRE(NamespacedKey.fromString("pickaria:millionaire")),
	FIRST_REFORGE(NamespacedKey.fromString("pickaria:reforge")),
	GET_REWARD(NamespacedKey.fromString("pickaria:reward")),
	ROOT(NamespacedKey.fromString("pickaria:root")),
	JOB_S_TIER(NamespacedKey.fromString("pickaria:s_tier")),
	SMALL_CITY(NamespacedKey.fromString("pickaria:small_city")),
	SMALL_TOWN(NamespacedKey.fromString("pickaria:small_town")),
	VILLAGER_BUY(NamespacedKey.fromString("pickaria:villager_buy")),
	JOB_X_TIER(NamespacedKey.fromString("pickaria:x_tier"));

	private fun getAdvancement() = this.namespace?.let { Bukkit.getAdvancement(it) }

	fun grant(player: Player) {
		getAdvancement()?.let {
			player.grantAdvancement(it)
		}
	}
}