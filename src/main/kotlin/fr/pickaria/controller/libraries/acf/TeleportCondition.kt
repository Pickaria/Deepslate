package fr.pickaria.controller.libraries.acf

import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.ConditionFailedException
import fr.pickaria.controller.teleport.canTeleport
import fr.pickaria.controller.teleport.hasOnGoingTeleport
import fr.pickaria.model.teleport.teleportConfig

fun BukkitCommandManager.teleportCondition() {
    commandConditions.addCondition("can_teleport") { context ->
        val player = context.issuer.player
        if (player.hasOnGoingTeleport()) {
            throw ConditionFailedException("Une téléportation est déjà en cours")
        }

        if (!player.canTeleport(teleportConfig.delayBetweenTeleports)) {
            throw ConditionFailedException("Veuillez attendre avant votre prochaine téléportation.")
        }
    }
}