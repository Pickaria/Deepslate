package fr.pickaria.model.miniblocks

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
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

val miniBlocksConfig = Yaml.default.decodeFromStream<MiniBlocksConfig>(getResourceFileStream("miniblocks.yml"))
