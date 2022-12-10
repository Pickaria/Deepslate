package fr.pickaria.artefact

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

internal fun Player.getWornArtefacts(): Map<EquipmentSlot, Artefact> = mutableMapOf<EquipmentSlot, Artefact>().apply {
	inventory.let { inv ->
		inv.helmet?.artefact?.let { put(EquipmentSlot.HEAD, it) }
		inv.chestplate?.artefact?.let { put(EquipmentSlot.CHEST, it) }
		inv.leggings?.artefact?.let { put(EquipmentSlot.LEGS, it) }
		inv.boots?.artefact?.let { put(EquipmentSlot.FEET, it) }

		inv.itemInMainHand.let { item ->
			if (!item.isReceptacle()) {
				item.artefact?.let { put(EquipmentSlot.HAND, it) }
			}
		}
		inv.itemInOffHand.let { item ->
			if (!item.isReceptacle()) {
				item.artefact?.let { put(EquipmentSlot.HAND, it) }
			}
		}
	}
}

/**
 * Returns true if the player is wearing the given artefact.
 */
internal fun Player.isWearingArtefact(artefact: Artefact): Boolean {
	inventory.let { inv ->
		inv.helmet?.artefact?.let { if (it === artefact) return true }
		inv.chestplate?.artefact?.let { if (it === artefact) return true }
		inv.leggings?.artefact?.let { if (it === artefact) return true }
		inv.boots?.artefact?.let { if (it === artefact) return true }

		inv.itemInMainHand.let { item ->
			if (!item.isReceptacle()) {
				item.artefact?.let { if (it === artefact) return true }
			}
		}
		inv.itemInOffHand.let { item ->
			if (!item.isReceptacle()) {
				item.artefact?.let { if (it === artefact) return true }
			}
		}
	}

	return false
}