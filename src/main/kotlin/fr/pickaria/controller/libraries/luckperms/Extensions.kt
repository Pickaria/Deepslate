package fr.pickaria.controller.libraries.luckperms

import fr.pickaria.controller.libraries.bukkit.getRegistration
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player


val luckPermsProvider = getRegistration<LuckPerms>()!!.provider

val Player.luckPermsUser: User
	get() = luckPermsProvider.getPlayerAdapter(Player::class.java).getUser(this)

fun User.save() {
	luckPermsProvider.userManager.saveUser(this);
}

fun Group.displayName() = displayName?.let {
	MiniMessage(it).toComponent()
} ?: Component.text(name)

fun getGroup(name: String) = luckPermsProvider.groupManager.getGroup(name)

val Player.group: Group?
	get() = getGroup(luckPermsUser.primaryGroup)
