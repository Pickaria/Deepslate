package fr.pickaria.controller.town

import fr.pickaria.model.town.bannerNamespace
import fr.pickaria.model.town.townNamespace
import org.bukkit.Material
import org.bukkit.block.Banner
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

val Material.isBanner
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
			this == Material.BLACK_BANNER ||
			this == Material.WHITE_WALL_BANNER ||
			this == Material.ORANGE_WALL_BANNER ||
			this == Material.MAGENTA_WALL_BANNER ||
			this == Material.LIGHT_BLUE_WALL_BANNER ||
			this == Material.YELLOW_WALL_BANNER ||
			this == Material.LIME_WALL_BANNER ||
			this == Material.PINK_WALL_BANNER ||
			this == Material.GRAY_WALL_BANNER ||
			this == Material.LIGHT_GRAY_WALL_BANNER ||
			this == Material.CYAN_WALL_BANNER ||
			this == Material.PURPLE_WALL_BANNER ||
			this == Material.BLUE_WALL_BANNER ||
			this == Material.BROWN_WALL_BANNER ||
			this == Material.GREEN_WALL_BANNER ||
			this == Material.RED_WALL_BANNER ||
			this == Material.BLACK_WALL_BANNER

fun ItemStack.isTownBanner() = type.isBanner && itemMeta.persistentDataContainer.has(bannerNamespace)

var ItemStack.townId: Int?
	get() = itemMeta.persistentDataContainer.get(
		townNamespace, PersistentDataType.INTEGER
	)
	set(value) {
		value?.let {
			editMeta { meta ->
				meta.persistentDataContainer.set(townNamespace, PersistentDataType.INTEGER, it)
			}
		} ?: editMeta {
			it.persistentDataContainer.remove(townNamespace)
		}
	}

var Block.townId: Int?
	get() = if (type.isBanner) {
		(state as Banner).persistentDataContainer.get(townNamespace, PersistentDataType.INTEGER)
	} else {
		null
	}
	set(value) {
		if (type.isBanner) {
			val banner = state as Banner
			value?.let {
				banner.persistentDataContainer.set(townNamespace, PersistentDataType.INTEGER, it)
			} ?: banner.persistentDataContainer.remove(townNamespace)
			banner.update()
		}
	}

val BlockState.townId: Int?
	get() = if (type.isBanner) {
		(this as Banner).persistentDataContainer.get(
			townNamespace, PersistentDataType.INTEGER
		)
	} else null
