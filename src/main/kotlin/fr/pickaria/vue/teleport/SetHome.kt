package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.model.teleport.*
import fr.pickaria.model.teleport.Homes.locationX
import fr.pickaria.model.teleport.Homes.locationY
import fr.pickaria.model.teleport.Homes.locationZ
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.h2.value.ValueVarchar
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.locks.Condition


@CommandAlias("sethome")
@CommandPermission("pickaria.command.sethome")
class SetHome(private val plugin: JavaPlugin) : BaseCommand() {

    companion object {

        private val TAG = "HAS_TP_ONGOING"


        private val EXCLUDED_BLOCKS = setOf(
            // Oceans
            Biome.OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,

            // Rivers
            Biome.RIVER,
            Biome.FROZEN_RIVER,

            // Powder snow biomes
//			Biome.SNOWY_SLOPES,
//			Biome.GROVE,
        )

        private val EXCLUDED_MATERIALS = setOf(
            Material.MAGMA_BLOCK,
            Material.CACTUS,
            Material.POWDER_SNOW,
            Material.WATER,

            )
    }

    @Default
    @Description("Créer un home.")
    fun onDefault(player: Player, @Default("home") name: String) {

        val location = player.location

        val home = transaction {
            Home.find {
                (Homes.playerUuid eq player.uniqueId) and (Homes.homeName eq name)

            }.firstOrNull()
        }


        val homeSafe = EXCLUDED_BLOCKS.contains(player.location.world.getBiome(location))

        //println(homeSafe)
//        println("contain")
//        println(EXCLUDED_BLOCKS.contains(player.location.world.getBiome(location)))
//        println(EXCLUDED_MATERIALS.contains(location.block.type))
        // println(player.world.name)
        // println(player.location.world.getBiome(player.location))

        //if(!homeSafe){
//            println(homeExist)
        if (home == null) {
            transaction {
                Home.new {
                    playerUuid = player.uniqueId
                    homeName = name
                    world = player.world.uid
                    locationX = location.blockX
                    locationY = location.blockY
                    locationZ = location.blockZ
                }
            }
            player.sendMessage(teleportConfig.homeRegisterationConfirm)
        }else{
            player.sendMessage("wola t con")
        }

//        }else{
//            println(homeSafe)
//            throw ConditionFailedException("Vous ne pouvez pas créer de home ici.")
//        }


    }
}