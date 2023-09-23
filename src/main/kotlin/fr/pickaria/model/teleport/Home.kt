package fr.pickaria.model.teleport

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Homes : IntIdTable() {
    val playerUuid = uuid("player_uuid")
    val homeName = varchar("home_name", 9)
    val world = uuid("world")
    val locationX = integer("location_x")
    val locationY = integer("location_y")
    val locationZ = integer("location_z")
    val material = varchar("material", 256)

    init {
        index(true, playerUuid, homeName)
    }
}

class Home(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Home>(Homes)

    var playerUuid by Homes.playerUuid
    var homeName by Homes.homeName
    var world by Homes.world
    var locationX by Homes.locationX
    var locationY by Homes.locationY
    var locationZ by Homes.locationZ
    var material by Homes.material
}