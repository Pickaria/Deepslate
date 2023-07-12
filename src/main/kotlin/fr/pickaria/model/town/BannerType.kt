package fr.pickaria.model.town

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

enum class BannerType(val material: Material) {
	WHITE(Material.WHITE_BANNER),
	ORANGE(Material.ORANGE_BANNER),
	MAGENTA(Material.MAGENTA_BANNER),
	LIGHT_BLUE(Material.LIGHT_BLUE_BANNER),
	YELLOW(Material.YELLOW_BANNER),
	LIME(Material.LIME_BANNER),
	PINK(Material.PINK_BANNER),
	GRAY(Material.GRAY_BANNER),
	LIGHT_GRAY(Material.LIGHT_GRAY_BANNER),
	CYAN(Material.CYAN_BANNER),
	PURPLE(Material.PURPLE_BANNER),
	BLUE(Material.BLUE_BANNER),
	BROWN(Material.BROWN_BANNER),
	GREEN(Material.GREEN_BANNER),
	RED(Material.RED_BANNER),
	BLACK(Material.BLACK_BANNER);

	fun item(): ItemStack {
		val itemStack = ItemStack(material)

		itemStack.editMeta {
			it.persistentDataContainer.set(bannerNamespace, PersistentDataType.BYTE, 1)
		}

		return itemStack
	}
}