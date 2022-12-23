package fr.pickaria.model.shop

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.datasources.getResourceFileStream
import fr.pickaria.model.serializers.SoundSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound


@Serializable
data class ShopConfig(
	@Serializable(with = SoundSerializer::class)
	@SerialName("trade_select_sound")
	val tradeSelectSound: Sound,

	@Serializable(with = SoundSerializer::class)
	@SerialName("trade_sound")
	val tradeSound: Sound,

	@Serializable(with = SoundSerializer::class)
	@SerialName("open_sound")
	val openSound: Sound,

	@Serializable(with = SoundSerializer::class)
	@SerialName("close_sound")
	val closeSound: Sound,

	@Serializable(with = SoundSerializer::class)
	@SerialName("grind_place_sound")
	val grindPlaceSound: Sound,

	@SerialName("grind_loss")
	val grindLoss: Double,

	@SerialName("grind_coin_value")
	val grindCoinValue: Double,

	val villagers: Map<String, VillagerConfig>,
)

val shopConfig = Yaml.default.decodeFromStream<ShopConfig>(getResourceFileStream("shop.yml"))