package fr.pickaria.artefact

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
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

fun isArtefact(itemStack: ItemStack): Boolean = getArtefact(itemStack) != null

internal fun getWornArtefacts(player: Player): Map<EquipmentSlot, Artefact> = mutableMapOf<EquipmentSlot, Artefact>().apply {
	player.inventory.let { inv ->
		inv.helmet?.let { getArtefact(it) }?.let { put(EquipmentSlot.HEAD, it) }
		inv.chestplate?.let { getArtefact(it) }?.let { put(EquipmentSlot.CHEST, it) }
		inv.leggings?.let { getArtefact(it) }?.let { put(EquipmentSlot.LEGS, it) }
		inv.boots?.let { getArtefact(it) }?.let { put(EquipmentSlot.FEET, it) }
		getArtefact(inv.itemInMainHand)?.let { put(EquipmentSlot.HAND, it) }
		getArtefact(inv.itemInOffHand)?.let { put(EquipmentSlot.OFF_HAND, it) }
	}
}