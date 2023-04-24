package fr.pickaria.model.teleport

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.stringParam

object Homes : IntIdTable() { // DSL: https://github.com/JetBrains/Exposed/wiki/DSL
    val playerUuid = uuid("player_uuid").uniqueIndex()
    val homeName = varchar("home_name", 9)
    val world = varchar("world", 20)
    val locationX = integer("location_x")
    val locationY = integer("location_y")
    val locationZ = integer("location_z")
}

class Home(id: EntityID<Int>) : IntEntity(id) { // DAO: https://github.com/JetBrains/Exposed/wiki/DAO
    companion object : IntEntityClass<Home>(Homes)

    var playerUuid by Homes.playerUuid
    var homeName by Homes.homeName
    var world by Homes.world
    var locationX by Homes.locationX
    var locationY by Homes.locationY
    var locationZ by Homes.locationZ
}