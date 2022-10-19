package fr.pickaria.shard

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.configuration.file.FileConfiguration

internal class ShopConfig(config: FileConfiguration) {
	val pickariteLabel = miniMessage.deserialize(config.getString("pickarite.label")!!)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
	val pickariteLore = config.getStringList("pickarite.lore").map {
		miniMessage.deserialize(it)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
	}
	val shopName = miniMessage.deserialize(config.getString("shop_name")!!)

	val tradeSelectSound = Sound.sound(Key.key(config.getString("sounds.trade_select")!!), Sound.Source.MASTER, 1f, 1f)
	val tradeSound = Sound.sound(Key.key(config.getString("sounds.trade")!!), Sound.Source.MASTER, 1f, 1f)
	val openSound = Sound.sound(Key.key(config.getString("sounds.open")!!), Sound.Source.MASTER, 1f, 1f)
	val closeSound = Sound.sound(Key.key(config.getString("sounds.close")!!), Sound.Source.MASTER, 1f, 1f)
	val grindPlaceSound = Sound.sound(Key.key(config.getString("sounds.grind_place")!!), Sound.Source.MASTER, 1f, 1f)
	val grindSound = Sound.sound(Key.key(config.getString("sounds.grind")!!), Sound.Source.MASTER, 1f, 1f)
}