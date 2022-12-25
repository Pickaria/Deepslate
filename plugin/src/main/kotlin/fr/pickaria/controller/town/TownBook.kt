package fr.pickaria.controller.town

import fr.pickaria.model.town.townConfig
import fr.pickaria.model.town.townNamespace
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.persistence.PersistentDataType

fun getTownBook(town: TownController, owner: Player): ItemStack {
	val item = ItemStack(Material.WRITTEN_BOOK)
	val meta = item.itemMeta as BookMeta

	meta.persistentDataContainer.set(townNamespace, PersistentDataType.INTEGER, town.id)

	val title = town.flag.itemMeta.displayName()!!
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

	meta.title(title)
	meta.author(owner.name())
	meta.pages(listOf(Component.empty()))

	item.itemMeta = meta

	return item
}

fun Player.openTownBook(town: TownController) {
	val meta = town.flag.itemMeta

	val page = MiniMessage(townConfig.page) {
		"town-name" to meta.displayName()!!
	}.message

	val book = Book.builder()
		.title(meta.displayName()!!)
		.pages(page)

	openBook(book)
}

fun ItemStack.isTownBook() = type == Material.WRITTEN_BOOK && itemMeta.persistentDataContainer.has(townNamespace)
