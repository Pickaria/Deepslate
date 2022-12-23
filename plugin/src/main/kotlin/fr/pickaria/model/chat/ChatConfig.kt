package fr.pickaria.model.chat

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.datasources.getResourceFileStream
import fr.pickaria.model.serializers.MiniMessageSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class ChatConfig(
	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("chat_format")
	val chatFormat: Component,
	@Serializable(with = MiniMessageSerializable::class)
	val join: Component,
	@Serializable(with = MiniMessageSerializable::class)
	val quit: Component,
	@SerialName("motd")
	val messageOfTheDay: List<String>,
)

val chatConfig = Yaml.default.decodeFromStream<ChatConfig>(getResourceFileStream("chat.yml"))
