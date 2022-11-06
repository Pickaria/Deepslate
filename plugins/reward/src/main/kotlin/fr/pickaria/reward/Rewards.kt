package fr.pickaria.reward

import io.papermc.paper.inventory.ItemRarity
import org.bukkit.Material
import org.bukkit.NamespacedKey

enum class Rewards(val title: String, val rarity: ItemRarity, val lootTable: NamespacedKey, val material: Material) {
	COMMON(
		"Récompense commune",
		ItemRarity.COMMON,
		NamespacedKey("pickaria", "common"),
		Material.WHITE_SHULKER_BOX
	),
	UNCOMMON(
		"Récompense peu commune",
		ItemRarity.UNCOMMON,
		NamespacedKey("pickaria", "uncommon"),
		Material.YELLOW_SHULKER_BOX
	),
	RARE(
		"Récompense rare",
		ItemRarity.RARE,
		NamespacedKey("pickaria", "rare"),
		Material.CYAN_SHULKER_BOX
	),
	EPIC(
		"Récompense épique",
		ItemRarity.EPIC,
		NamespacedKey("pickaria", "epic"),
		Material.MAGENTA_SHULKER_BOX
	),
}