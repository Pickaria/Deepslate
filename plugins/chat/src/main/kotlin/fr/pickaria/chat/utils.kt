package fr.pickaria.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player


fun Player.updateDisplayName() {
	val displayName = this.prefix()
		.append(this.name().color(NamedTextColor.WHITE))
		.append(this.suffix())
	this.displayName(displayName)
}

var Player.prefix: String
	get() = chat?.getPlayerPrefix(this) ?: ""
	set(value) = chat?.setPlayerPrefix(this, value) ?: Unit

var Player.suffix: String
	get() = chat?.getPlayerSuffix(this) ?: ""
	set(value) = chat?.setPlayerSuffix(this, value) ?: Unit

fun Player.prefix(): Component = this.prefix.let {
	if (it.isNotEmpty()) {
		miniMessage.deserialize(it).append(Component.text(" "))
	} else {
		Component.empty()
	}
}

fun Player.suffix(): Component = this.suffix.let {
	if (it.isNotEmpty()) {
		Component.text(" ").append(miniMessage.deserialize(it))
	} else {
		Component.empty()
	}
}