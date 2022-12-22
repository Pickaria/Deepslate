package fr.pickaria

import fr.pickaria.artefact.ArtefactConfig
import fr.pickaria.shared.ConfigProvider
import fr.pickaria.shops.VillagerConfig
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component


class Rarity : ConfigProvider() {
	val color: String by this
	val name: Component by this
	val attributes: Int by this
}

object Config : ConfigProvider() {
	// Artefacts
	val artefactReceptacleName: String by this
	private val artefacts by sectionLoader<ArtefactConfig>()
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

	// Villagers
	val villagers by sectionLoader<VillagerConfig>()
}