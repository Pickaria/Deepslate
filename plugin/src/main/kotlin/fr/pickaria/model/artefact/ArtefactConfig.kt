package fr.pickaria.model.artefact

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.datasources.getResourceFileStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtefactConfig(
	@SerialName("artefact_receptacle_name")
	val artefactReceptacleName: String,
	val artefacts: Map<String, Artefact>,
)

val artefactConfig = Yaml.default.decodeFromStream<ArtefactConfig>(getResourceFileStream("artefact.yml"))
