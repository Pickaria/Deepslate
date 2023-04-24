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

    @SerialName("home_registeration_confirm")
    val homeRegisterationConfirm: String,

    @SerialName("home_deletion_confirm")
    val homeDeletionConfirm: String,

    @SerialName("home_replace_confirm")
    val homeReplaceConfirm: String,

    @SerialName("rtp_multiplier")
    val rtpMultiplier: Int,

    @SerialName("spawn_multiplier")
    val spawnMultiplier: Int,
)

val teleportConfig = Yaml.default.decodeFromStream<TeleportConfiguration>(getResourceFileStream("teleport.yml"))
