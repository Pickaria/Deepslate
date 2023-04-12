package fr.pickaria.controller.rank

import fr.pickaria.controller.libraries.luckperms.getGroup
import fr.pickaria.controller.libraries.luckperms.group
import fr.pickaria.model.rank.Rank
import fr.pickaria.model.rank.rankConfig
import net.luckperms.api.model.group.Group
import org.bukkit.entity.Player

fun Rank.toGroup() = getGroup(groupName)
	?: throw NoSuchElementException("Group `$groupName` is not found, maybe you forgot to add it in LuckPerms?")

fun Group.toRank() = rankConfig.ranks[name]

fun Player.calculateRankUpgradePrice(nextRank: Rank): Int =
	(nextRank.price - (group?.toRank()?.price ?: 0)).coerceAtLeast(0)
