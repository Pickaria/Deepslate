package fr.pickaria.vue.chat

import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.audience.Audience

fun Audience.updateTabHeaderAndFooter() {
	val header = MiniMessage("<gold><b>Pickaria<newline><white><b>PLAY.PICKARIA.FR<newline>").toComponent()

	val footer =
		MiniMessage("<newline><gold>Site web : <gray>www.pickaria.fr<newline><gold>Discord : <gray>discord.gg/YR6fVaS").toComponent()

	sendPlayerListHeaderAndFooter(header, footer)
}