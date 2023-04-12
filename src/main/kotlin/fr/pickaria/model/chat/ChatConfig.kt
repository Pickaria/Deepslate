package fr.pickaria.model.chat

import fr.pickaria.model.config
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class ChatConfig(
	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("chat_format")
	val chatFormat: Component,
	@Serializable(with = MiniMessageSerializer::class)
	val join: Component,
	@Serializable(with = MiniMessageSerializer::class)
	val quit: Component,
	@SerialName("motd")
	val messageOfTheDay: List<String>,
)

val chatConfig = config<ChatConfig>("chat.yml")
