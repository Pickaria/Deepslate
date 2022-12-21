package fr.pickaria

import fr.pickaria.shared.ConfigProvider
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import fr.pickaria.Config as ArtefactConfig

class Artefact : ConfigProvider() {
	val label by miniMessageDeserializer
	private val description: List<String> by this
	val target: EnchantmentTarget by this
	private val rarity: ItemRarity by this
	val value: Int by this

	private val fullDescription by lazy {
		listOf(label.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)) + description.map {
			MiniMessage(it).message.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		}
	}

	fun createReceptacle(): ItemStack {
		val material: Material = when (rarity) {
			ItemRarity.COMMON -> Material.COPPER_INGOT
			ItemRarity.UNCOMMON -> Material.IRON_INGOT
			ItemRarity.RARE -> Material.GOLD_INGOT
			ItemRarity.EPIC -> Material.NETHERITE_INGOT
		}

		val itemStack = ItemStack(material)

		itemStack.itemMeta = itemStack.itemMeta.apply {
			addEnchant(GlowEnchantment.instance, 1, true)
			persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, section!!.name)
			persistentDataContainer.set(receptacleNamespace, PersistentDataType.BYTE, 1)
			displayName(
				Component.text(ArtefactConfig.artefactReceptacleName, rarity.color)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			)

			lore(fullDescription)
		}

		return itemStack
	}
}

class Rarity : ConfigProvider() {
	val color: String by this
	val name: Component by this
	val attributes: Int by this
}

object Config : ConfigProvider() {
	// Artefacts
	val artefactReceptacleName: String by this
	private val artefacts by sectionLoader<Artefact>()
	val lazyArtefacts by lazy {
		artefacts
	}

	// Reforge
	private val rarities by sectionLoader<Rarity>()
	val minimumAttribute: Double by this
	val maximumAttribute: Double by this
	val chargedLapisDescription: List<String> by this
	val chargedLapisName: String by this
	val enchantSound: Sound by this

	val lowestRarity by lazy {
		rarities.values.minByOrNull { it.attributes } ?: throw RuntimeException("Could not get default rarity.")
	}
	val sortedRarities by lazy {
		rarities.values.sortedByDescending { it.attributes }
	}

	// Shard
	val tradeSelectSound: Sound by this
	val tradeSound: Sound by this
	val openSound: Sound by this
	val closeSound: Sound by this
	val grindPlaceSound: Sound by this
	val grindLoss: Double by this
	val grindCoinValue: Double by this
}