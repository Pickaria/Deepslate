package fr.pickaria.model.miniblocks

import fr.pickaria.model.config
import fr.pickaria.model.serializers.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MiniBlocksConfig(
	@SerialName("miniblocks")
	val miniBlocks: List<MiniBlock>,

	@SerialName("default_price")
	val defaultPrice: Double,

	@SerialName("buy_message_singular")
	val buyMessageSingular: String,

	@SerialName("buy_message_plural")
	val buyMessagePlural: String,

	@Serializable(with = UUIDSerializer::class)
	val uuid: UUID,
) {
	val sortedMiniBlocks by lazy {
		miniBlocks.sortedBy {
			it.material.name
		}
	}
}

val miniBlocksConfig = config<MiniBlocksConfig>("miniblocks.yml")
