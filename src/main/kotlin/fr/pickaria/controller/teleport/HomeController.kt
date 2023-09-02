package fr.pickaria.controller.teleport

import fr.pickaria.model.teleport.Home
import fr.pickaria.model.teleport.Homes
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

fun Player.getHomeNames(startsWith: String) =
    transaction {
        Home.find { (Homes.playerUuid eq uniqueId) and (Homes.homeName like "${startsWith}%") }.map { it.homeName }
    }
