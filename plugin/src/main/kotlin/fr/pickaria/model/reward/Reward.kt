package fr.pickaria.model.reward

import fr.pickaria.controller.reward.RewardController
import fr.pickaria.model.serializers.AdvancementSerializer
import fr.pickaria.model.serializers.LootTableSerializer
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.advancement.Advancement
import org.bukkit.loot.LootTable

@Serializable
data class Reward(
	@Serializable(with = LootTableSerializer::class)
	@SerialName("loot_table")
	val lootTable: LootTable,
	val material: Material,
	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,
	val keys: Int,
	val shards: Int,
	val purchasable: Boolean = false,
	val description: List<String>,
	val type: String,
	@Serializable(with = AdvancementSerializer::class)
	val advancement: Advancement? = null,
)

fun Reward.toController(): RewardController = RewardController(this)