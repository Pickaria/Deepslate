package fr.pickaria.controller.libraries

import fr.pickaria.controller.libraries.events.getRegistration
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player


private val luckPermsProvider = getRegistration<LuckPerms>()!!.provider

val Player.luckPermsUser: User
	get() = luckPermsProvider.getPlayerAdapter(Player::class.java).getUser(this)

fun User.save() {
	luckPermsProvider.userManager.saveUser(this);
}
