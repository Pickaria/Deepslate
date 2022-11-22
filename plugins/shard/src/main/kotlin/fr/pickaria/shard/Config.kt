package fr.pickaria.shard

import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.sound.Sound

object Config: ConfigProvider() {
	val pickariteLore: MutableList<String> by this
	val collectShardMessage: String by this
	val tradeSelectSound: Sound by this
	val tradeSound: Sound by this
	val openSound: Sound by this
	val closeSound: Sound by this
	val grindPlaceSound: Sound by this
	val grindSound: Sound by this
	val currencyNameSingular: String by this
	val currencyNamePlural: String by this
	val grindLoss: Double by this
	val grindCoinValue: Double by this
	val noShardToTrade by this.miniMessageDeserializer
	val noShardToTradeSound: Sound by this
}