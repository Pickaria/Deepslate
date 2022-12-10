package fr.pickaria.artefact

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Returns the artefact of the item if any.
 */
val ItemStack.artefact
	get(): Artefact? =
	itemMeta?.persistentDataContainer?.get(artefactNamespace, PersistentDataType.STRING)?.let {
		try {
			Config.artefacts[it]
		} catch (_: IllegalArgumentException) {
			null
		}
	}

/**
 * Returns the artefact configuration of the item if any.
 */
@Deprecated("", ReplaceWith("getartefact"))
fun ItemStack.getArtefactConfig() = artefact

/**
 * Returns true if the given item is an artefact, it can be a receptacle as well.
 */
fun ItemStack.isArtefact(): Boolean = artefact != null

/**
 * Returns true if the given item is a receptacle.
 */
fun ItemStack.isReceptacle(): Boolean = itemMeta?.persistentDataContainer?.get(receptacleNamespace, PersistentDataType.BYTE) != null
