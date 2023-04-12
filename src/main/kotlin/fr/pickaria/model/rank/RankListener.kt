package fr.pickaria.model.rank

import fr.pickaria.controller.libraries.luckperms.subscribeToLuckPerms
import fr.pickaria.shared.updateDisplayName
import net.luckperms.api.event.node.NodeMutateEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit.getPlayer
import org.bukkit.plugin.java.JavaPlugin


fun JavaPlugin.rankListener() = subscribeToLuckPerms<NodeMutateEvent> {
	if (isUser) {
		getPlayer((target as User).uniqueId)?.let {
			it.updateDisplayName()
		}
	}
}
