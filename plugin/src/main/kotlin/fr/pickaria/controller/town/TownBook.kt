package fr.pickaria.controller.town

import com.palmergames.bukkit.towny.`object`.Town
import fr.pickaria.model.town.townConfig
import fr.pickaria.model.town.townNamespace
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.persistence.PersistentDataType

fun getTownBook(town: Town): ItemStack {
	val item = ItemStack(Material.WRITTEN_BOOK)
	val meta = item.itemMeta as BookMeta

	meta.persistentDataContainer.set(townNamespace, PersistentDataType.STRING, town.uuid.toString())

	meta.title(Component.text(town.name))
	meta.author(Component.text(town.founder))
	meta.pages(listOf(Component.empty()))

	item.itemMeta = meta

	return item
}

fun Player.openTownBook(town: Town) {
	val page = MiniMessage(townConfig.page) {
		"town-name" to town.name
	}.message

	val book = Book.builder()
		.title(Component.text(town.name))
		.pages(page)

	openBook(book)
}

fun ItemStack.isTownBook() = type == Material.WRITTEN_BOOK && itemMeta.persistentDataContainer.has(townNamespace)
