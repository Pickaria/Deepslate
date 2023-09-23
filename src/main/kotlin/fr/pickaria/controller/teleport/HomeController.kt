package fr.pickaria.controller.teleport

import fr.pickaria.model.teleport.Home
import fr.pickaria.model.teleport.Homes
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

fun Player.getHomeNames(startsWith: String) =
    transaction {
        Home.find { (Homes.playerUuid eq uniqueId) and (Homes.homeName like "${startsWith}%") }.map { it.homeName }
    }

data class HomeEntry(val title: Component, val material: Material, val name: String)

fun Player.getHomes() =
    transaction {
        Home.find { (Homes.playerUuid eq uniqueId) }
            .map {
                HomeEntry(
                    Component.text(it.homeName),
                    Material.getMaterial(it.material) ?: Material.BEDROCK,
                    it.homeName
                )
            }
    }

fun Player.homeCount() =
    transaction {
        Home.find { (Homes.playerUuid eq uniqueId) }.count()
    }
