package fr.pickaria.chat

import fr.pickaria.shared.ConfigProvider

object Config : ConfigProvider() {
	val chatFormat by miniMessageDeserializer
	val join by miniMessageDeserializer
	val quit by miniMessageDeserializer
	val motd: List<String> by this

	val messageOfTheDay by lazy {
		motd.joinToString("<newline>")
	}
}