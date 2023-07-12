package fr.pickaria.model.artefact

import fr.pickaria.model.config
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtefactConfig(
	@SerialName("artefact_receptacle_name")
	val artefactReceptacleName: String,
	val artefacts: Map<String, Artefact>,
)

val artefactConfig = config<ArtefactConfig>("artefact.yml")
