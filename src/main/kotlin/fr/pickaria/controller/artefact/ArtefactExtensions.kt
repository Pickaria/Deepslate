package fr.pickaria.controller.artefact

import fr.pickaria.model.artefact.Artefact
import fr.pickaria.model.artefact.artefactConfig
import fr.pickaria.model.artefact.artefactNamespace
import fr.pickaria.model.artefact.receptacleNamespace
import fr.pickaria.shared.GlowEnchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Returns the artefact of the item if any.
 */
val ItemStack.artefact
	get(): Artefact? =
		itemMeta?.persistentDataContainer?.get(artefactNamespace, PersistentDataType.STRING)?.let {
			artefactConfig.artefacts[it.lowercase()]
		}

/**
 * Returns true if the given item is an artefact, it can be a receptacle as well.
 */
fun ItemStack.isArtefact(): Boolean = artefact != null

/**
 * Returns true if the given item is a receptacle.
 */
fun ItemStack.isReceptacle(): Boolean =
	itemMeta?.persistentDataContainer?.get(receptacleNamespace, PersistentDataType.BYTE) != null

/**
 * Sets the artefact on the ItemStack.
 */
fun ItemStack.setArtefact(artefact: Artefact): ItemStack {
	editMeta {
		it.addEnchant(GlowEnchantment.instance, 1, true)
		it.persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, artefact.type.name)
	}

	return this
}

/**
 * Removes an artefact from the ItemStack.
 */
fun ItemStack.removeArtefact(): ItemStack {
	editMeta {
		it.removeEnchant(GlowEnchantment.instance)
		it.persistentDataContainer.remove(artefactNamespace)
	}

	return this
}