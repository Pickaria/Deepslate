package fr.pickaria.model.rank

import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material
import kotlin.time.Duration

@Serializable
data class Rank(
	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,

	@SerialName("group_name")
	val groupName: String,

	val duration: Duration,
	val price: Int,
	val description: List<String>,
	val material: Material,
) {
	val permission: String
		get() = "group.$groupName"
}
