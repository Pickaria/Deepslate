package fr.pickaria.controller.town

import fr.pickaria.model.town.bannerNamespace
import fr.pickaria.model.town.townNamespace
import org.bukkit.Material
import org.bukkit.block.Banner
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private val Material.isBanner
	get() = this == Material.WHITE_BANNER ||
			this == Material.ORANGE_BANNER ||
			this == Material.MAGENTA_BANNER ||
			this == Material.LIGHT_BLUE_BANNER ||
			this == Material.YELLOW_BANNER ||
			this == Material.LIME_BANNER ||
			this == Material.PINK_BANNER ||
			this == Material.GRAY_BANNER ||
			this == Material.LIGHT_GRAY_BANNER ||
			this == Material.CYAN_BANNER ||
			this == Material.PURPLE_BANNER ||
			this == Material.BLUE_BANNER ||
			this == Material.BROWN_BANNER ||
			this == Material.GREEN_BANNER ||
			this == Material.RED_BANNER ||
			this == Material.BLACK_BANNER

fun ItemStack.isTownBanner() = type.isBanner && itemMeta.persistentDataContainer.has(bannerNamespace)

val ItemStack.townId: Int?
	get() = itemMeta.persistentDataContainer.get(
		townNamespace,
		PersistentDataType.INTEGER
	)

val Block.townId: Int?
	get() = if (type.isBanner) (state as Banner).persistentDataContainer.get(
		townNamespace,
		PersistentDataType.INTEGER
	) else null
