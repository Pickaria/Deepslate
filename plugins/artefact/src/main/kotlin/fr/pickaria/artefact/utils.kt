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

fun createArtefact(artefact: Artefact): ItemStack {
	val material: Material = when (artefact.rarity) {
		ItemRarity.COMMON -> Material.COPPER_INGOT
		ItemRarity.UNCOMMON -> Material.IRON_INGOT
		ItemRarity.RARE -> Material.GOLD_INGOT
		ItemRarity.EPIC -> Material.NETHERITE_INGOT
	}

	val itemStack = ItemStack(material)

	itemStack.itemMeta = itemStack.itemMeta.apply {
		addEnchant(GlowEnchantment.instance, 1, true)
		persistentDataContainer.set(namespace, PersistentDataType.STRING, artefact.name)
		displayName(
			Component.text("Réceptacle d'artefact", artefact.rarity.color)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)

		lore(listOf(artefact.displayName) + artefact.lore)
	}

	return itemStack
}