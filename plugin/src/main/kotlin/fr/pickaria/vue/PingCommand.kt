package fr.pickaria.vue

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

@CommandAlias("ping")
class PingCommand : BaseCommand() {
	@Default
	fun onPing(player: Player) {
		player.sendMessage(Component.text("Pong!"))
	}
}