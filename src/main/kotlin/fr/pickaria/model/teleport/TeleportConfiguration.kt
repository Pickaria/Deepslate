package fr.pickaria.model.teleport

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeleportConfiguration(
    @SerialName("delay_between_teleports")
    val delayBetweenTeleports: Int,

    @SerialName("delay_before_teleports")
    val delayBeforeTeleports: Long,

    @SerialName("message_before_teleports")
    val messageBeforeTeleport: String,
)

val teleportConfig = Yaml.default.decodeFromStream<TeleportConfiguration>(getResourceFileStream("teleport.yml"))
