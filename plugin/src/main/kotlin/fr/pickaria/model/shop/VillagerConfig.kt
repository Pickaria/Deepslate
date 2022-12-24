package fr.pickaria.model.shop

import fr.pickaria.controller.shop.VillagerController
import fr.pickaria.model.serializers.MiniMessageSerializer
import fr.pickaria.model.serializers.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.entity.Villager
import java.util.*

@Serializable
data class VillagerConfig(
	val offers: ShopOffer? = null,

	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,

	@SerialName("villager_type")
	val villagerType: Villager.Type,

	val profession: Villager.Profession,

	@Serializable(with = UUIDSerializer::class)
	val uuid: UUID, // TODO: Make uuid optional and generate a random one

	val menu: String? = null,
)

fun VillagerConfig.toController() = VillagerController(this)