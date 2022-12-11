package fr.pickaria.artefact

import fr.pickaria.artefactNamespace
import fr.pickaria.receptacleNamespace
import fr.pickaria.shared.GlowEnchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Returns the artefact of the item if any.
 */
val ItemStack.artefact
	get(): Artefact? =
		itemMeta?.persistentDataContainer?.get(artefactNamespace, PersistentDataType.STRING)?.let {
			Config.lazyArtefacts[it]
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
fun ItemStack.isReceptacle(): Boolean =
	itemMeta?.persistentDataContainer?.get(receptacleNamespace, PersistentDataType.BYTE) != null

/**
 * Sets the artefact on the ItemStack.
 */
fun ItemStack.setArtefact(artefact: Artefact): ItemStack {
	editMeta {
		it.addEnchant(GlowEnchantment.instance, 1, true)
		it.persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, artefact.section!!.name)
	}

	return this
}