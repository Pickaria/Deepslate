package fr.pickaria.controller.artefact

import fr.pickaria.model.artefact.Artefact
import fr.pickaria.model.artefact.artefactConfig
import fr.pickaria.model.artefact.artefactNamespace
import fr.pickaria.model.artefact.receptacleNamespace
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ArtefactController(val model: Artefact) {
	private val fullDescription by lazy {
		listOf(model.label.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)) + model.description.map {
			MiniMessage(it).toComponent().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		}
	}

	fun createReceptacle(): ItemStack {
		val material: Material = when (model.rarity) {
			ItemRarity.COMMON -> Material.COPPER_INGOT
			ItemRarity.UNCOMMON -> Material.IRON_INGOT
			ItemRarity.RARE -> Material.GOLD_INGOT
			ItemRarity.EPIC -> Material.NETHERITE_INGOT
		}

		val itemStack = ItemStack(material)

		itemStack.itemMeta = itemStack.itemMeta.apply {
			addEnchant(GlowEnchantment.instance, 1, true)
			persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, model.type.name)
			persistentDataContainer.set(receptacleNamespace, PersistentDataType.BYTE, 1)
			displayName(
				Component.text(artefactConfig.artefactReceptacleName, model.rarity.color)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			)

			lore(fullDescription)
		}

		return itemStack
	}
}