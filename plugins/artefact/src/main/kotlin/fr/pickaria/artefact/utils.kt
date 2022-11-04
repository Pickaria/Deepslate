package fr.pickaria.artefact

import fr.pickaria.shared.GlowEnchantment
import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Returns the artefact of the item if any.
 */
private fun getArtefact(itemStack: ItemStack): Artefact? =
	itemStack.itemMeta?.persistentDataContainer?.get(artefactNamespace, PersistentDataType.STRING)?.let {
		try {
			Artefact.valueOf(it)
		} catch (_: IllegalArgumentException) {
			null
		}
	}

/**
 * Returns the artefact configuration of the item if any.
 */
fun getArtefactConfig(itemStack: ItemStack): ArtefactConfig.Configuration? = getArtefact(itemStack)?.getConfig()

/**
 * Returns true if the given item is an artefact, it can be a receptacle as well.
 */
fun isArtefact(itemStack: ItemStack): Boolean = getArtefactConfig(itemStack) != null

/**
 * Returns true if the given item is a receptacle.
 */
fun isReceptacle(itemStack: ItemStack): Boolean = itemStack.itemMeta?.persistentDataContainer?.get(receptacleNamespace, PersistentDataType.BYTE) != null

/**
 * Returns a Map of all artefact per equipment slot.
 */
internal fun getWornArtefacts(player: Player): Map<EquipmentSlot, ArtefactConfig.Configuration> = mutableMapOf<EquipmentSlot, ArtefactConfig.Configuration>().apply {
	player.inventory.let { inv ->
		inv.helmet?.let { getArtefactConfig(it) }?.let { put(EquipmentSlot.HEAD, it) }
		inv.chestplate?.let { getArtefactConfig(it) }?.let { put(EquipmentSlot.CHEST, it) }
		inv.leggings?.let { getArtefactConfig(it) }?.let { put(EquipmentSlot.LEGS, it) }
		inv.boots?.let { getArtefactConfig(it) }?.let { put(EquipmentSlot.FEET, it) }

		inv.itemInMainHand.let { item ->
			if (!isReceptacle(item)) {
				getArtefactConfig(item)?.let { put(EquipmentSlot.HAND, it) }
			}
		}
		inv.itemInOffHand.let { item ->
			if (!isReceptacle(item)) {
				getArtefactConfig(item)?.let { put(EquipmentSlot.HAND, it) }
			}
		}
	}
}

/**
 * Returns true if the player is wearing the given artefact.
 */
internal fun isWearingArtefact(player: Player, artefact: Artefact): Boolean {
	player.inventory.let { inv ->
		inv.helmet?.let { getArtefact(it) }?.let { if (it == artefact) return true }
		inv.chestplate?.let { getArtefact(it) }?.let { if (it == artefact) return true }
		inv.leggings?.let { getArtefact(it) }?.let { if (it == artefact) return true }
		inv.boots?.let { getArtefact(it) }?.let { if (it == artefact) return true }

		inv.itemInMainHand.let { item ->
			if (!isReceptacle(item)) {
				getArtefact(item)?.let { if (it == artefact) return true }
			}
		}
		inv.itemInOffHand.let { item ->
			if (!isReceptacle(item)) {
				getArtefact(item)?.let { if (it == artefact) return true }
			}
		}
	}

	return false
}

/**
 * Creates a new receptacle item containing the given artefact.
 */
fun createArtefactReceptacle(artefact: ArtefactConfig.Configuration): ItemStack {
	val material: Material = when (artefact.rarity) {
		ItemRarity.COMMON -> Material.COPPER_INGOT
		ItemRarity.UNCOMMON -> Material.IRON_INGOT
		ItemRarity.RARE -> Material.GOLD_INGOT
		ItemRarity.EPIC -> Material.NETHERITE_INGOT
	}

	val itemStack = ItemStack(material)

	itemStack.itemMeta = itemStack.itemMeta.apply {
		addEnchant(GlowEnchantment.instance, 1, true)
		persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, artefact.key.name)
		persistentDataContainer.set(receptacleNamespace, PersistentDataType.BYTE, 1)
		displayName(
			Component.text(artefactConfig.artefactReceptacleName, artefact.rarity.color)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)

		lore(listOf(artefact.label) + artefact.lore)
	}

	return itemStack
}