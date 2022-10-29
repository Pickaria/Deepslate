package fr.pickaria.menu

import net.kyori.adventure.text.Component

class MenuLore {
	var rightClick: String? = null
	var leftClick: String? = null
	var description: List<String> = emptyList()
	var keyValues: Map<String, Any> = emptyMap()

	companion object {
		inline fun build(block: MenuLore.() -> Unit) = MenuLore().apply(block).build()
	}

	fun build(): List<Component> {
		val lore: MutableList<String> = mutableListOf()

		description.forEach {
			lore.add("§7$it")
		}

		if (description.isNotEmpty() && keyValues.isNotEmpty()) {
			lore.add("")
		}

		keyValues.forEach { (key, value) ->
			lore.add("§6$key : §7$value")
		}

		if ((!leftClick.isNullOrBlank() || !rightClick.isNullOrBlank()) && (keyValues.isNotEmpty() || description.isNotEmpty())) {
			lore.add("")
		}

		leftClick?.let { lore.add(it) }
		rightClick?.let { lore.add(it) }

		return lore.map { Component.text(it) }
	}
}