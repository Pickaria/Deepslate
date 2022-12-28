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
	val dailyPointsToCollect: Int,

	@SerialName("reward_per_day")
	val rewardPerDay: Int,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("daily_reward_is_ready")
	val dailyRewardIsReady: Component,

	@Serializable(with = SoundSerializer::class)
	@SerialName("daily_reward_sound")
	val dailyRewardSound: Sound,
)

val rewardConfig = Yaml.default.decodeFromStream<RewardConfig>(getResourceFileStream("reward.yml"))
