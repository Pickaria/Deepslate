package fr.pickaria.controller.town

import fr.pickaria.model.town.bookNamespace
import fr.pickaria.model.town.townConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

fun ItemStack.isTownBook() = type == Material.WRITTEN_BOOK && itemMeta.persistentDataContainer.has(bookNamespace)

fun Player.openTownBook() {
	val book = transaction { town }?.let {
		Book.builder()
			.pages(Component.text(it.name))
			.build()
	} ?: Book.builder()
		.pages(MiniMessage(townConfig.noTown).message)
		.build()

	openBook(book)
}
