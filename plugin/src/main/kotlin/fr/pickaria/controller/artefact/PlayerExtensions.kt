package fr.pickaria.controller.artefact

import fr.pickaria.model.artefact.ArtefactType
import org.bukkit.advancement.Advancement
import org.bukkit.entity.Player

/**
 * Returns true if the player is wearing the given artefact.
 */
internal fun Player.isWearingArtefact(artefact: ArtefactType): Boolean {
	inventory.let { inv ->
		inv.helmet?.artefact?.let { if (it.type === artefact) return true }
		inv.chestplate?.artefact?.let { if (it.type === artefact) return true }
		inv.leggings?.artefact?.let { if (it.type === artefact) return true }
		inv.boots?.artefact?.let { if (it.type === artefact) return true }

		inv.itemInMainHand.let { item ->
			if (!item.isReceptacle()) {
				item.artefact?.let { if (it.type === artefact) return true }
			}
		}
		inv.itemInOffHand.let { item ->
			if (!item.isReceptacle()) {
				item.artefact?.let { if (it.type === artefact) return true }
			}
		}
	}

	return false
}

fun Player.grantAdvancement(advancement: Advancement) {
	val progress = getAdvancementProgress(advancement)
	progress.remainingCriteria.forEach {
		progress.awardCriteria(it)
	}
}