package fr.pickaria.model.teleport

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Histories : IntIdTable() { // DSL: https://github.com/JetBrains/Exposed/wiki/DSL
    val playerUuid = uuid("player_uuid").uniqueIndex()
    val lastTeleport = datetime("last_teleport")
}

class History(id: EntityID<Int>) : IntEntity(id) { // DAO: https://github.com/JetBrains/Exposed/wiki/DAO
    companion object : IntEntityClass<History>(Histories)

    var playerUuid by Histories.playerUuid
    var lastTeleport by Histories.lastTeleport
}