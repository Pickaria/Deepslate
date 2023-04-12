package fr.pickaria.model.town

import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta


@Serializable
data class Flag(
	val material: Material,

	val patterns: List<SerializedPattern>,
)

fun Flag.toBanner() = ItemStack(material).apply {
	editMeta { meta ->
		(meta as BannerMeta).patterns = patterns.map { pattern ->
			pattern.toPattern()
		}
	}
}