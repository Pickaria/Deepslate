import co.aikar.commands.ConditionFailedException
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin

fun returnMetaDataTpa(p:Plugin,key: String,sender: Player): Player {

    val senderName = FixedMetadataValue((p), sender.name)

    try {
        val metadataList: List<MetadataValue>? = sender.getMetadata(key)
        val requestSenderValue: MetadataValue = metadataList!![0]
        val requestSender: String = requestSenderValue.asString()
        val recipient: Player = Bukkit.getPlayerExact(requestSender)!!
        return recipient
    } catch (e: Exception){
        throw ConditionFailedException("Aucune demande de téléportation n'est en cours")
    }
}

fun createMetaDataTpa(plugin: Plugin, sender: Player, recipient: Player) {

    val TargetTpa = "targetTpa" //recipient
    val SenderTpa = "senderTpa" //sender

    val senderName = FixedMetadataValue((plugin), sender.name)
    val recipientName = FixedMetadataValue((plugin), recipient.name)
try {
    recipient.setMetadata(TargetTpa,senderName)
    sender.setMetadata(SenderTpa,recipientName)
} catch (e: Exception){
    throw ConditionFailedException("Erreur, un des joueurs n'existe pas")
}
    return
}

fun createMetaDataTpTag(plugin: Plugin, sender: Player) {

    val TAG = "HAS_TP_ONGOING"

    val senderName = FixedMetadataValue((plugin), sender.name)
    try {
        sender.setMetadata(TAG,senderName)
    } catch (e: Exception){
        throw ConditionFailedException("Erreur, un des joueurs n'existe pas")
    }
    return
}