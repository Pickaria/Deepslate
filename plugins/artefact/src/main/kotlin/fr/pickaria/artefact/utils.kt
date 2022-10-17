package fr.pickaria.artefact

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


fun getArtefact(itemStack: ItemStack): Artefact? =
	itemStack.itemMeta?.persistentDataContainer?.get(namespace, PersistentDataType.STRING)?.let {
		try {
			Artefact.valueOf(it)
		} catch (_: IllegalArgumentException) {
			null
		}
	}

fun getWornArtefacts(player: Player): MutableList<Artefact> =
	player.inventory.armorContents.filterNotNull().mapNotNull { item ->
		getArtefact(item)
	}.toMutableList()
		.apply {
			getArtefact(player.inventory.itemInMainHand)?.let { add(it) }
			getArtefact(player.inventory.itemInOffHand)?.let { add(it) }
		}