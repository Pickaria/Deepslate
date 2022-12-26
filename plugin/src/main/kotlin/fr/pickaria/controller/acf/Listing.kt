package fr.pickaria.controller.acf

import co.aikar.commands.InvalidCommandArgument
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.tag.Tag

private const val PAGE_SIZE = 8

typealias RowBuilder<T> = (index: Int, entity: T) -> MiniMessage.KeyValuesBuilder.() -> Unit

fun <T> listing(
	header: String,
	row: String,
	footer: String,
	command: String,
	page: Int,
	entities: List<T>,
	count: Long,
	builder: RowBuilder<T>
): TextComponent.Builder {
	val pageStart = page * PAGE_SIZE
	val maxPage = count / PAGE_SIZE

	if (entities.isEmpty()) {
		throw InvalidCommandArgument(economyConfig.notMuchPages)
	}

	val component = Component.text()
		.append(
			+MiniMessage(header) {
				"page" to (page + 1)
				"max" to (maxPage + 1)
			}
		)

	entities.forEachIndexed { index, town ->
		component
			.append(Component.newline())
			.append(
				+MiniMessage(row, builder(index, town))
			)
	}

	if (count > pageStart + PAGE_SIZE) {
		component
			.append(Component.newline())
			.append(
				+MiniMessage(footer) {
					"next-page" to Tag.styling(ClickEvent.runCommand("/$command ${page + 1}"))
					"page" to (page + 1)
				}
			)
	}

	return component
}