package fr.pickaria.artefact

import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.EnchantmentTarget

class ArtefactConfig(config: FileConfiguration) {
	data class Configuration(
		val key: Artefact,
		val label: Component,
		val lore: List<Component>,
		val target: EnchantmentTarget,
		val rarity: ItemRarity,
		val value: Int,
	)

	val artefacts: Map<Artefact, Configuration> = config.getConfigurationSection("artefacts")!!
		.getKeys(false)
		.map { Artefact.valueOf(it.uppercase()) }
		.associateWith { artefact ->
			Bukkit.getLogger().info("Loading artefact '$artefact'")

			val section = config.getConfigurationSection("artefacts.${artefact.name.lowercase()}")!!

			Configuration(
				artefact,
				miniMessage.deserialize(section.getString("label")!!)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				section.getStringList("description").map {
					miniMessage.deserialize(it)
						.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				},
				EnchantmentTarget.valueOf(section.getString("target")!!),
				ItemRarity.valueOf(section.getString("rarity")!!),
				section.getInt("value"),
			)
		}

	val artefactReceptacleName = config.getString("artefact_receptacle_name")!!
}