package fr.pickaria.artefact

import fr.pickaria.shared.ConfigProvider
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import fr.pickaria.artefact.Config as ArtefactConfig

class Artefact : ConfigProvider() {
	val label by this.miniMessageDeserializer
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

object Config : ConfigProvider() {
	val artefactReceptacleName: String by this
	val artefacts by sectionLoader<Artefact>()
}