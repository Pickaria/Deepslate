package fr.pickaria.artefact

enum class Artefact {
	FLAME_COSMETICS,
	STEALTH,
	ICE_THORNS,
	LUCKY,
	;

	fun getConfig(): ArtefactConfig.Configuration? = artefactConfig.artefacts[this]
}