package fr.pickaria.chat

import fr.pickaria.shared.setupChat
import fr.pickaria.shared.setupEconomy
import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin


internal val economy: Economy? = setupEconomy()
internal val chat: Chat? = setupChat()
internal val miniMessage: MiniMessage = MiniMessage.miniMessage();
internal lateinit var chatConfig: ChatConfig

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		chatConfig = ChatConfig(this.config)

		economy?.let {
			playerList(this)
		}

		server.pluginManager.let {
			it.registerEvents(PlayerJoin(), this)
			it.registerEvents(ChatFormat(), this)
			it.registerEvents(Motd(), this)
		}

		logger.info("Chat plugin loaded!")
	}
}
