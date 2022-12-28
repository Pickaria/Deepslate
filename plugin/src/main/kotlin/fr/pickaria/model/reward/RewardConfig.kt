package fr.pickaria.model.reward

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
import fr.pickaria.model.serializers.MiniMessageSerializer
import fr.pickaria.model.serializers.SoundSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component


@Serializable
data class RewardConfig(
	val rewards: Map<String, Reward>,

	@SerialName("not_enough_money")
	val notEnoughMoney: String,

	@SerialName("cant_purchase_reward")
	val cantPurchaseReward: String,

	@Serializable(with = SoundSerializer::class)
	@SerialName("reward_close_sound")
	val rewardCloseSound: Sound,

	@Serializable(with = SoundSerializer::class)
	@SerialName("reward_open_sound")
	val rewardOpenSound: Sound,

	@SerialName("daily_points_to_collect")
	val dailyPointsToCollect: Int, // TODO: Use a permission to define this per player basis

	@SerialName("reward_per_day")
	val rewardPerDay: Int, // TODO: Use a permission to define this per player basis

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("daily_reward_is_ready")
	val dailyRewardIsReady: Component,

	@Serializable(with = SoundSerializer::class)
	@SerialName("daily_reward_sound")
	val dailyRewardSound: Sound,

	@SerialName("special_days")
	val specialDays: Map<MonthDay, String>,

	@SerialName("streak_rewards")
	val streakRewards: List<String>,

	@SerialName("streak_reward_every")
	val streakRewardEvery: Int,

	@SerialName("default_reward")
	val defaultReward: String,
)

val rewardConfig = Yaml.default.decodeFromStream<RewardConfig>(getResourceFileStream("reward.yml"))
