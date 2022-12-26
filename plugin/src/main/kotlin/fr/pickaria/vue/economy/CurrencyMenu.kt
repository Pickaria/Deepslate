package fr.pickaria.vue.economy

import fr.pickaria.menu.ItemBuilderUnsafe
import fr.pickaria.menu.menu
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.toController
import org.bukkit.Material

@OptIn(ItemBuilderUnsafe::class)
fun currencyMenu() = menu("currency") {
	val items = Credit.toController().items(page.toDouble())
	val bundle = Credit.toController().bundle(page.toDouble())

	item {
		material = Material.BUNDLE
		setMeta(bundle.itemMeta)
		title = bundle.itemMeta.displayName()
		lore = bundle.itemMeta.lore()!!
	}

	items.forEachIndexed { index, it ->
		item {
			slot = index + 1
			material = it.type
			setMeta(it.itemMeta)
			amount = it.amount
			title = it.itemMeta.displayName()
			lore = it.itemMeta.lore()!!
		}
	}
}