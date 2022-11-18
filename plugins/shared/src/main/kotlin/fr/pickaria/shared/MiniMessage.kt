package fr.pickaria.shared

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class MiniMessage(value: String, init: (KeyValuesBuilder.() -> Unit)? = null) {
	companion object {
		internal val miniMessage: MiniMessage = MiniMessage.miniMessage()
	}

	private var placeholders: MutableList<TagResolver> = mutableListOf()
	val message: Component

	inner class KeyValuesBuilder {
		infix fun String.to(value: Component) {
			val placeholder = Placeholder.component(this, value)
			placeholders.add(placeholder)
		}

		infix fun String.to(value: Any) {
			val placeholder = Placeholder.unparsed(this, value.toString())
			placeholders.add(placeholder)
		}
	}

	operator fun unaryPlus(): Component = message

	init {
		if (init != null) {
			KeyValuesBuilder().init()
		}
		message = miniMessage.deserialize(value, *placeholders.toTypedArray())
	}

	fun send(player: Audience) {
		player.sendMessage(message)
	}
}