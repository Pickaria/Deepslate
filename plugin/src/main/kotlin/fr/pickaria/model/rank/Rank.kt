package fr.pickaria.model.rank

import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Serializable
data class Rank(
	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,
	val duration: Long,
	val price: Int,
	val description: List<String>,
	val material: Material,
	@SerialName("group_name")
	val groupName: String,
) {
	val permission: String
		get() = "group.$groupName"
}
