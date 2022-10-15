package fr.pickaria.chat

import org.bukkit.configuration.file.FileConfiguration

class ChatConfig(config: FileConfiguration) {
	val chatFormat = miniMessage.deserialize(config.getString("chat_format")!!)
	val join = miniMessage.deserialize(config.getString("join")!!)
	val quit = miniMessage.deserialize(config.getString("quit")!!)
}